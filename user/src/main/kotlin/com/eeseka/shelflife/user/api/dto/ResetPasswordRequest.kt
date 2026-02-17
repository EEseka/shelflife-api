package com.eeseka.shelflife.user.api.dto

import com.eeseka.shelflife.user.api.util.Password
import jakarta.validation.constraints.NotBlank

data class ResetPasswordRequest(
    @field:NotBlank
    val token: String,
    @field:Password
    val newPassword: String
)
