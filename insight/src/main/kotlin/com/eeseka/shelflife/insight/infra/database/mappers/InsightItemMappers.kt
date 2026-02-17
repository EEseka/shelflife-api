package com.eeseka.shelflife.insight.infra.database.mappers

import com.eeseka.shelflife.insight.domain.models.InsightItem
import com.eeseka.shelflife.insight.infra.database.entities.HealthInfoEmbeddable
import com.eeseka.shelflife.insight.infra.database.entities.InsightItemEntity

fun InsightItemEntity.toDomain(): InsightItem {
    return InsightItem(
        id = id!!,
        userId = userId,
        barcode = barcode,
        name = name,
        brand = brand,
        imageUrl = imageUrl,
        quantity = quantity,
        quantityUnit = quantityUnit,
        actionDate = actionDate,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt,

        // Flatten the Nested Health Info back to the Domain level
        nutriScore = healthInfo?.nutriScore,
        novaGroup = healthInfo?.novaGroup,
        ecoScore = healthInfo?.ecoScore
    )
}

fun InsightItem.toEntity(): InsightItemEntity {
    return InsightItemEntity(
        id = id,
        userId = userId,
        barcode = barcode,
        name = name,
        brand = brand,
        imageUrl = imageUrl,
        quantity = quantity,
        quantityUnit = quantityUnit,
        actionDate = actionDate,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt,

        // Group Domain fields into the Embeddable object
        healthInfo = HealthInfoEmbeddable(
            nutriScore = nutriScore,
            novaGroup = novaGroup,
            ecoScore = ecoScore
        )
    )
}