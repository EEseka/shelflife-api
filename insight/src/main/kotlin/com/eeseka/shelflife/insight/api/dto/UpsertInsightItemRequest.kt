package com.eeseka.shelflife.insight.api.dto

import com.eeseka.shelflife.insight.domain.models.InsightStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class UpsertInsightItemRequest(
    @field:NotBlank(message = "ID is required")
    val id: String,

    @field:NotBlank(message = "Barcode is required")
    val barcode: String,

    @field:NotBlank(message = "Name is required")
    val name: String,

    val brand: String? = null,
    val imageUrl: String? = null,

    @field:Positive(message = "Quantity must be positive")
    val quantity: Double,

    @field:NotBlank(message = "Quantity unit is required")
    val quantityUnit: String,

    @field:NotBlank(message = "Action date is required")
    val actionDate: String,

    val status: InsightStatus,

    @field:Positive(message = "Created timestamp must be valid")
    val createdAt: Long,
    @field:Positive(message = "Updated timestamp must be valid")
    val updatedAt: Long,

    val nutriScore: String? = null,
    val novaGroup: Int? = null,
    val ecoScore: String? = null
)