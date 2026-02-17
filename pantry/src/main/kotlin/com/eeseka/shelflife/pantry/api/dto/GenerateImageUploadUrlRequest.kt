package com.eeseka.shelflife.pantry.api.dto

import com.eeseka.shelflife.common.domain.type.PantryItemId
import jakarta.validation.constraints.NotBlank

data class GenerateImageUploadUrlRequest(
    @field:NotBlank
    val itemId: PantryItemId,
    @field:NotBlank
    val mimeType: String
)