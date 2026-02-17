package com.eeseka.shelflife.insight.domain.models

import com.eeseka.shelflife.common.domain.type.InsightItemId
import com.eeseka.shelflife.common.domain.type.UserId
import java.time.Instant
import java.time.LocalDate

data class InsightItem(
    val id: InsightItemId,
    val userId: UserId,

    val barcode: String,
    val name: String,
    val brand: String? = null,
    val imageUrl: String? = null,

    // --- QUANTITY ---
    val quantity: Double = 1.0,
    val quantityUnit: String = "",

    // --- The Analytics ---
    val status: InsightStatus,
    val actionDate: LocalDate,

    // --- METADATA ---
    val createdAt: Instant,
    val updatedAt: Instant,

    // --- HEALTH & INSIGHTS ---
    val nutriScore: String? = null,
    val novaGroup: Int? = null,
    val ecoScore: String? = null
)