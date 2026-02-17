package com.eeseka.shelflife.pantry.domain.models

import com.eeseka.shelflife.common.domain.type.PantryItemId
import com.eeseka.shelflife.common.domain.type.UserId
import java.time.Instant
import java.time.LocalDate

data class PantryItem(
    val id: PantryItemId,
    val userId: UserId,

    val barcode: String,
    val name: String,
    val brand: String? = null,
    val imageUrl: String? = null,

    // --- QUANTITY ---
    val quantity: Double = 1.0,
    val quantityUnit: String = "",
    val packagingSize: String? = null,

    // --- DATES ---
    val expiryDate: LocalDate,
    val purchaseDate: LocalDate,
    val openDate: LocalDate? = null,

    // --- STORAGE ---
    val storageLocation: StorageLocation,

    // --- METADATA ---
    val createdAt: Instant,
    val updatedAt: Instant,

    // --- HEALTH & INSIGHTS ---
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