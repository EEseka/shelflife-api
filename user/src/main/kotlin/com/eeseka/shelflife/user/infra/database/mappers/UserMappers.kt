package com.eeseka.shelflife.user.infra.database.mappers

import com.eeseka.shelflife.user.domain.model.User
import com.eeseka.shelflife.user.infra.database.entities.UserEntity

fun UserEntity.toUser(): User {
    return User(
        id = id!!,
        username = username,
        email = email,
        hasEmailVerified = hasVerifiedEmail,
        profilePhotoUrl = profilePhotoUrl
    )
}