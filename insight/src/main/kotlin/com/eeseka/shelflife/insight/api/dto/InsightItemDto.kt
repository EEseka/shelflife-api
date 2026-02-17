package com.eeseka.shelflife.insight.api.dto

import com.eeseka.shelflife.insight.domain.models.InsightStatus

data class InsightItemDto(
    val id: String,
    val barcode: String,
    val name: String,
    val brand: String? = null,
    val imageUrl: String? = null,

    // Quantity
    val quantity: Double,
    val quantityUnit: String,

    // Dates (ISO-8601 Strings: "2023-12-25")
    val actionDate: String,

    val status: InsightStatus,

    // Metadata
    val createdAt: Long,
    val updatedAt: Long,

    // Health
    val nutriScore: String? = null,
    val novaGroup: Int? = null,
    val ecoScore: String? = null
)