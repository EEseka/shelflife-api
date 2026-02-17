package com.eeseka.shelflife.user.api.dto

import com.eeseka.shelflife.user.api.util.Password
import jakarta.validation.constraints.NotBlank

data class ChangePasswordRequest(
    @field:NotBlank
    val oldPassword: String,
    @field:Password
    val newPassword: String
)
