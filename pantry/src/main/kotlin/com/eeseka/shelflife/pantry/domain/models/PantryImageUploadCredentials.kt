package com.eeseka.shelflife.pantry.domain.models

import java.time.Instant

data class PantryImageUploadCredentials(
    val uploadUrl: String, // The secure link to upload the file
    val publicUrl: String, // The link to VIEW the file after upload
    val headers: Map<String, String>, // Auth headers for Supabase
    val expiresAt: Instant // When the upload link dies
)