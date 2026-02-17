package com.eeseka.shelflife.user.infra.database.entities

import com.eeseka.shelflife.common.domain.type.UserId
import com.eeseka.shelflife.user.infra.database.utils.AuthProvider
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(
    name = "users",
    schema = "user_service",
    indexes = [
        Index(name = "idx_users_email", columnList = "email"),
        Index(name = "idx_users_username", columnList = "username"),
    ]
)
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UserId? = null,
    @Column(nullable = false, unique = true)
    var email: String,
    @Column(nullable = false, unique = true)
    var username: String,
    @Column(nullable = true)
    var hashedPassword: String?,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val authProvider: AuthProvider,
    @Column(nullable = false)
    var hasVerifiedEmail: Boolean = false,
    @Column(nullable = true)
    var profilePhotoUrl: String? = null,
    @CreationTimestamp
    var createdAt: Instant = Instant.now(),
    @UpdateTimestamp
    var updatedAt: Instant = Instant.now(),
)