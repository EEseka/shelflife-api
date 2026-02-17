package com.eeseka.shelflife.pantry.api.dto

import java.time.Instant

data class PantryImageUploadResponse(
    val uploadUrl: String,
    val publicUrl: String,
    val headers: Map<String, String>,
    val expiresAt: Instant
)