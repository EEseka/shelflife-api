package com.eeseka.shelflife.insight.api.mappers

import com.eeseka.shelflife.common.domain.type.UserId
import com.eeseka.shelflife.insight.api.dto.InsightItemDto
import com.eeseka.shelflife.insight.api.dto.UpsertInsightItemRequest
import com.eeseka.shelflife.insight.domain.models.InsightItem
import java.time.Instant
import java.time.LocalDate
import java.util.*

fun InsightItem.toDto(): InsightItemDto {
    return InsightItemDto(
        id = id.toString(),
        barcode = barcode,
        name = name,
        brand = brand,
        imageUrl = imageUrl,
        quantity = quantity,
        quantityUnit = quantityUnit,
        actionDate = actionDate.toString(),
        status = status,
        createdAt = createdAt.toEpochMilli(),
        updatedAt = updatedAt.toEpochMilli(),
        nutriScore = nutriScore,
        novaGroup = novaGroup,
        ecoScore = ecoScore
    )
}

fun UpsertInsightItemRequest.toDomain(userId: UserId): InsightItem {
    return InsightItem(
        id = UUID.fromString(id),
        userId = userId,
        barcode = barcode,
        name = name,
        brand = brand,
        imageUrl = imageUrl,
        quantity = quantity,
        quantityUnit = quantityUnit,
        actionDate = LocalDate.parse(actionDate),
        status = status,
        createdAt = Instant.ofEpochMilli(createdAt),
        updatedAt = Instant.ofEpochMilli(updatedAt),
        nutriScore = nutriScore,
        novaGroup = novaGroup,
        ecoScore = ecoScore
    )
}