package com.eeseka.shelflife.pantry.api.dto

import com.eeseka.shelflife.pantry.domain.models.StorageLocation

data class PantryItemDto(
    val id: String,
    val barcode: String,
    val name: String,
    val brand: String? = null,
    val imageUrl: String? = null,

    // Quantity
    val quantity: Double,
    val quantityUnit: String,
    val packagingSize: String? = null,

    // Dates (ISO-8601 Strings: "2023-12-25")
    val expiryDate: String,
    val purchaseDate: String,
    val openDate: String? = null,

    val storageLocation: StorageLocation,

    // Metadata
    val createdAt: Long,
    val updatedAt: Long,

    // Health
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