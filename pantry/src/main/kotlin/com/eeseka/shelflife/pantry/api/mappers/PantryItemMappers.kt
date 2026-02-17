package com.eeseka.shelflife.pantry.api.mappers

import com.eeseka.shelflife.common.domain.type.UserId
import com.eeseka.shelflife.pantry.api.dto.PantryItemDto
import com.eeseka.shelflife.pantry.api.dto.UpsertPantryItemRequest
import com.eeseka.shelflife.pantry.domain.models.PantryItem
import java.time.Instant
import java.time.LocalDate
import java.util.*

fun PantryItem.toDto(): PantryItemDto {
    return PantryItemDto(
        id = id.toString(),
        barcode = barcode,
        name = name,
        brand = brand,
        imageUrl = imageUrl,
        quantity = quantity,
        quantityUnit = quantityUnit,
        packagingSize = packagingSize,
        expiryDate = expiryDate.toString(),
        purchaseDate = purchaseDate.toString(),
        openDate = openDate?.toString(),
        storageLocation = storageLocation,
        createdAt = createdAt.toEpochMilli(),
        updatedAt = updatedAt.toEpochMilli(),
        nutriScore = nutriScore,
        novaGroup = novaGroup,
        ecoScore = ecoScore,
        allergens = allergens,
        labels = labels,
        caloriesPer100g = caloriesPer100g,
        sugarPer100g = sugarPer100g,
        fatPer100g = fatPer100g,
        proteinPer100g = proteinPer100g
    )
}

fun UpsertPantryItemRequest.toDomain(userId: UserId): PantryItem {
    return PantryItem(
        id = UUID.fromString(id),
        userId = userId,
        barcode = barcode,
        name = name,
        brand = brand,
        imageUrl = imageUrl,
        quantity = quantity,
        quantityUnit = quantityUnit,
        packagingSize = packagingSize,
        expiryDate = LocalDate.parse(expiryDate),
        purchaseDate = LocalDate.parse(purchaseDate),
        openDate = openDate?.let { LocalDate.parse(it) },
        storageLocation = storageLocation,
        createdAt = Instant.ofEpochMilli(createdAt),
        updatedAt = Instant.ofEpochMilli(updatedAt),
        nutriScore = nutriScore,
        novaGroup = novaGroup,
        ecoScore = ecoScore,
        allergens = allergens,
        labels = labels,
        caloriesPer100g = caloriesPer100g,
        sugarPer100g = sugarPer100g,
        fatPer100g = fatPer100g,
        proteinPer100g = proteinPer100g
    )
}