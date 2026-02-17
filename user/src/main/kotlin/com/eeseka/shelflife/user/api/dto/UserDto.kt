package com.eeseka.shelflife.user.api.dto

import com.eeseka.shelflife.common.domain.type.UserId

data class UserDto(
    val id: UserId,
    val email: String,
    val username: String,
    val hasVerifiedEmail: Boolean,
    val profilePhotoUrl: String?
)