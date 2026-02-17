package com.eeseka.shelflife.pantry.api.mappers

import com.eeseka.shelflife.pantry.api.dto.PantryImageUploadResponse
import com.eeseka.shelflife.pantry.domain.models.PantryImageUploadCredentials

fun PantryImageUploadCredentials.toResponse(): PantryImageUploadResponse {
    return PantryImageUploadResponse(
        uploadUrl = uploadUrl,
        publicUrl = publicUrl,
        headers = headers,
        expiresAt = expiresAt
    )
}