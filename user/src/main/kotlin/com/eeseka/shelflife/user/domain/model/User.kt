package com.eeseka.shelflife.user.domain.model

import com.eeseka.shelflife.common.domain.type.UserId

data class User(
    val id: UserId,
    val username: String,
    val email: String,
    val hasEmailVerified: Boolean,
    val profilePhotoUrl: String?
)
