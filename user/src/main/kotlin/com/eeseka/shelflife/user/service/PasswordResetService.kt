package com.eeseka.shelflife.user.service

import com.eeseka.shelflife.common.domain.events.user.UserEvent
import com.eeseka.shelflife.common.domain.exception.InvalidTokenException
import com.eeseka.shelflife.common.domain.type.UserId
import com.eeseka.shelflife.common.infra.message_queue.EventPublisher
import com.eeseka.shelflife.user.domain.exception.InvalidAuthProviderException
import com.eeseka.shelflife.user.domain.exception.InvalidCredentialsException
import com.eeseka.shelflife.user.domain.exception.SamePasswordException
import com.eeseka.shelflife.user.domain.exception.UserNotFoundException
import com.eeseka.shelflife.user.infra.database.entities.PasswordResetTokenEntity
import com.eeseka.shelflife.user.infra.database.repositories.PasswordResetTokenRepository
import com.eeseka.shelflife.user.infra.database.repositories.RefreshTokenRepository
import com.eeseka.shelflife.user.infra.database.repositories.UserRepository
import com.eeseka.shelflife.user.infra.database.utils.AuthProvider
import com.eeseka.shelflife.user.infra.security.PasswordEncoder
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class PasswordResetService(
    private val userRepository: UserRepository,
    private val passwordResetTokenRepository: PasswordResetTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    @param:Value("\${shelflife.email.reset-password.expiry-minutes}")
    private val expiryMinutes: Long,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val eventPublisher: EventPublisher
) {
    @Transactional
    fun requestPasswordReset(email: String) {
        val user = userRepository.findByEmail(email) ?: return

        if (user.authProvider == AuthProvider.GOOGLE) {
            throw InvalidAuthProviderException("This account uses Google Sign-In. You cannot reset a password.")
        }

        passwordResetTokenRepository.invalidateActiveTokensForUser(user)

        val token = PasswordResetTokenEntity(
            user = user,
            expiresAt = Instant.now().plus(expiryMinutes, ChronoUnit.MINUTES),
        )
        passwordResetTokenRepository.save(token)

        eventPublisher.publish(
            event = UserEvent.RequestResetPassword(
                userId = user.id!!,
                email = user.email,
                username = user.username,
                passwordResetToken = token.token,
                expiresInMinutes = expiryMinutes
            )
        )
    }

    @Transactional
    fun resetPassword(token: String, newPassword: String) {
        val resetToken = passwordResetTokenRepository.findByToken(token)
            ?: throw InvalidTokenException("Invalid password reset token")

        if (resetToken.isUsed) {
            throw InvalidTokenException("Email verification token is already used.")
        }

        if (resetToken.isExpired) {
            throw InvalidTokenException("Email verification token has already expired.")
        }

        val user = resetToken.user

        // Safety check just in case a token was generated wrongly
        if (user.authProvider == AuthProvider.GOOGLE) {
            throw InvalidAuthProviderException("Google accounts cannot reset passwords.")
        }

        // Logic: Since the provider is EMAIL, hashedPassword MUST NOT be null.
        if (passwordEncoder.matches(newPassword, user.hashedPassword!!)) {
            throw SamePasswordException()
        }

        val hashedNewPassword = passwordEncoder.encode(newPassword)
        userRepository.save(
            user.apply {
                this.hashedPassword = hashedNewPassword!!
            }
        )

        passwordResetTokenRepository.save(
            resetToken.apply {
                this.usedAt = Instant.now()
            }
        )

        refreshTokenRepository.deleteByUserId(user.id!!)
    }

    @Transactional
    fun changePassword(
        userId: UserId,
        oldPassword: String,
        newPassword: String,
    ) {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw UserNotFoundException()

        // Google Users cannot change passwords
        if (user.authProvider == AuthProvider.GOOGLE) {
            throw InvalidAuthProviderException("You are logged in with Google. You cannot change a password you don't have.")
        }

        if (!passwordEncoder.matches(oldPassword, user.hashedPassword!!)) {
            throw InvalidCredentialsException()
        }

        if (oldPassword == newPassword) {
            throw SamePasswordException()
        }

        refreshTokenRepository.deleteByUserId(user.id!!)

        val newHashedPassword = passwordEncoder.encode(newPassword)
        userRepository.save(
            user.apply {
                this.hashedPassword = newHashedPassword!!
            }
        )
    }

    @Scheduled(cron = "0 0 3 * * *")
    fun cleanupExpiredTokens() {
        passwordResetTokenRepository.deleteByExpiresAtLessThan(
            now = Instant.now()
        )
    }
}