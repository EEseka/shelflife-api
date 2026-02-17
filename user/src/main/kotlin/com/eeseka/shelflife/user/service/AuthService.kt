package com.eeseka.shelflife.user.service

import com.eeseka.shelflife.common.domain.events.user.UserEvent
import com.eeseka.shelflife.common.domain.exception.InvalidTokenException
import com.eeseka.shelflife.common.domain.type.UserId
import com.eeseka.shelflife.common.infra.message_queue.EventPublisher
import com.eeseka.shelflife.common.service.JwtService
import com.eeseka.shelflife.user.domain.exception.*
import com.eeseka.shelflife.user.domain.model.AuthenticatedUser
import com.eeseka.shelflife.user.domain.model.User
import com.eeseka.shelflife.user.infra.database.entities.RefreshTokenEntity
import com.eeseka.shelflife.user.infra.database.entities.UserEntity
import com.eeseka.shelflife.user.infra.database.mappers.toUser
import com.eeseka.shelflife.user.infra.database.repositories.RefreshTokenRepository
import com.eeseka.shelflife.user.infra.database.repositories.UserRepository
import com.eeseka.shelflife.user.infra.database.utils.AuthProvider
import com.eeseka.shelflife.user.infra.security.PasswordEncoder
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.time.Instant
import java.util.*

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val emailVerificationService: EmailVerificationService,
    private val eventPublisher: EventPublisher,
    private val googleAuthService: GoogleAuthService
) {

    @Transactional
    fun register(email: String, username: String, password: String): User {
        val trimmedEmail = email.trim()
        val trimmedUsername = username.trim()

        val user = userRepository.findByEmailOrUsername(
            email = trimmedEmail,
            username = trimmedUsername
        )
        if (user != null) {
            throw UserAlreadyExistsException()
        }

        val savedUser = userRepository.saveAndFlush(
            UserEntity(
                email = trimmedEmail,
                username = trimmedUsername,
                hashedPassword = passwordEncoder.encode(password)!!,
                authProvider = AuthProvider.EMAIL
            )
        ).toUser()

        val token = emailVerificationService.createVerificationToken(trimmedEmail)

        eventPublisher.publish(
            event = UserEvent.Created(
                userId = savedUser.id,
                email = savedUser.email,
                username = savedUser.username,
                verificationToken = token.token
            )
        )

        return savedUser
    }

    fun login(
        email: String,
        password: String
    ): AuthenticatedUser {
        val user = userRepository.findByEmail(email.trim()) ?: throw InvalidCredentialsException()

        if (user.authProvider == AuthProvider.GOOGLE) {
            throw InvalidAuthProviderException("This email is associated with a Google account. Please sign in with Google.")
        }

        if (!passwordEncoder.matches(password, user.hashedPassword!!)) {
            throw InvalidCredentialsException()
        }

        if (!user.hasVerifiedEmail) {
            throw EmailNotVerifiedException()
        }

        return user.id?.let { userId ->
            val accessToken = jwtService.generateAccessToken(userId)
            val refreshToken = jwtService.generateRefreshToken(userId)

            storeRefreshToken(userId, refreshToken)

            AuthenticatedUser(
                user = user.toUser(),
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        } ?: throw UserNotFoundException()
    }

    @Transactional
    fun googleLogin(token: String): AuthenticatedUser {
        val googleUser = googleAuthService.verify(token)

        val existingUser = userRepository.findByEmail(googleUser.email)

        val user = existingUser ?: userRepository.save(
            UserEntity(
                email = googleUser.email,
                username = googleUser.username,
                hashedPassword = null,
                authProvider = AuthProvider.GOOGLE,
                hasVerifiedEmail = true, // Trusted from Google
                profilePhotoUrl = googleUser.pictureUrl
            )
        )

        return user.id?.let { userId ->
            val accessToken = jwtService.generateAccessToken(userId)
            val refreshToken = jwtService.generateRefreshToken(userId)

            storeRefreshToken(userId, refreshToken)

            AuthenticatedUser(
                user = user.toUser(),
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        } ?: throw UserNotFoundException()
    }

    @Transactional
    fun refresh(refreshToken: String): AuthenticatedUser {
        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw InvalidTokenException(message = "Invalid refresh token")
        }

        val userId = jwtService.getUserIdFromToken(refreshToken)
        val user = userRepository.findByIdOrNull(userId)
            ?: throw UserNotFoundException()

        val hashed = hashToken(refreshToken)

        return user.id?.let { userId ->
            refreshTokenRepository.findByUserIdAndHashedToken(
                userId = userId,
                hashedToken = hashed
            ) ?: throw InvalidTokenException("Invalid refresh token")

            refreshTokenRepository.deleteByUserIdAndHashedToken(
                userId = userId,
                hashedToken = hashed
            )

            val newAccessToken = jwtService.generateAccessToken(userId)
            val newRefreshToken = jwtService.generateRefreshToken(userId)

            storeRefreshToken(userId, newRefreshToken)

            AuthenticatedUser(
                user = user.toUser(),
                accessToken = newAccessToken,
                refreshToken = newRefreshToken
            )
        } ?: throw UserNotFoundException()
    }

    @Transactional
    fun logout(refreshToken: String) {
        val userId = jwtService.getUserIdFromToken(refreshToken)
        val hashed = hashToken(refreshToken)
        refreshTokenRepository.deleteByUserIdAndHashedToken(userId, hashed)
    }

    @Transactional
    fun deleteAccount(userId: UserId) {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw UserNotFoundException()

        userRepository.deleteById(userId)

        eventPublisher.publish(
            event = UserEvent.Deleted(
                userId = userId
            )
        )
    }

    private fun storeRefreshToken(userId: UserId, token: String) {
        val hashed = hashToken(token)
        val expiryMs = jwtService.refreshTokenValidityMs
        val expiresAt = Instant.now().plusMillis(expiryMs)

        refreshTokenRepository.save(
            RefreshTokenEntity(
                userId = userId,
                expiresAt = expiresAt,
                hashedToken = hashed
            )
        )
    }

    private fun hashToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }
}