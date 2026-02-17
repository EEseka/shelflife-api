package com.eeseka.shelflife.user.api.mappers

import com.eeseka.shelflife.user.api.dto.AuthenticatedUserDto
import com.eeseka.shelflife.user.api.dto.UserDto
import com.eeseka.shelflife.user.domain.model.AuthenticatedUser
import com.eeseka.shelflife.user.domain.model.User

fun AuthenticatedUser.toAuthenticatedUserDto(): AuthenticatedUserDto {
    return AuthenticatedUserDto(
        user = user.toUserDto(),
        accessToken = accessToken,
        refreshToken = refreshToken
    )
}

fun User.toUserDto(): UserDto {
    return UserDto(
        id = id,
        email = email,
        username = username,
        hasVerifiedEmail = hasEmailVerified,
        profilePhotoUrl = profilePhotoUrl
    )
}