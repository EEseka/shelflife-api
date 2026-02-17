package com.eeseka.shelflife.pantry.api.dto

import com.eeseka.shelflife.pantry.domain.models.StorageLocation
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class UpsertPantryItemRequest(
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

    val packagingSize: String? = null,

    @field:NotBlank(message = "Expiry date is required")
    val expiryDate: String,
    @field:NotBlank(message = "Purchase date is required")
    val purchaseDate: String,
    val openDate: String? = null,

    val storageLocation: StorageLocation,

    @field:Positive(message = "Created timestamp must be valid")
    val createdAt: Long,

    @field:Positive(message = "Updated timestamp must be valid")
    val updatedAt: Long,

    val nutriScore: String? = null,
    val novaGroup: Int? = null,
    val ecoScore: String? = null,
    val allergens: List<String> = emptyList(),
    val labels: List<String> = emptyList(),
    val caloriesPer100g: Int? = null,
    val sugarPer100g: Double? = null,
    val fatPer100g: Double? = null,
    val proteinPer100g: Double? = null
)