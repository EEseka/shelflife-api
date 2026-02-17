package com.eeseka.shelflife.pantry.infra.database.mappers

import com.eeseka.shelflife.pantry.domain.models.PantryItem
import com.eeseka.shelflife.pantry.infra.database.entities.HealthInfoEmbeddable
import com.eeseka.shelflife.pantry.infra.database.entities.PantryItemEntity

fun PantryItemEntity.toDomain(): PantryItem {
    return PantryItem(
        id = id!!,
        userId = userId,
        barcode = barcode,
        name = name,
        brand = brand,
        imageUrl = imageUrl,
        quantity = quantity,
        quantityUnit = quantityUnit,
        packagingSize = packagingSize,
        expiryDate = expiryDate,
        purchaseDate = purchaseDate,
        openDate = openDate,
        storageLocation = storageLocation,
        createdAt = createdAt,
        updatedAt = updatedAt,

        // Flatten the Nested Health Info back to the Domain level
        nutriScore = healthInfo?.nutriScore,
        novaGroup = healthInfo?.novaGroup,
        ecoScore = healthInfo?.ecoScore,
        allergens = healthInfo?.allergens ?: emptyList(),
        labels = healthInfo?.labels ?: emptyList(),
        caloriesPer100g = healthInfo?.caloriesPer100g,
        sugarPer100g = healthInfo?.sugarPer100g,
        fatPer100g = healthInfo?.fatPer100g,
        proteinPer100g = healthInfo?.proteinPer100g
    )
}

fun PantryItem.toEntity(): PantryItemEntity {
    return PantryItemEntity(
        id = id,
        userId = userId,
        barcode = barcode,
        name = name,
        brand = brand,
        imageUrl = imageUrl,
        quantity = quantity,
        quantityUnit = quantityUnit,
        packagingSize = packagingSize,
        expiryDate = expiryDate,
        purchaseDate = purchaseDate,
        openDate = openDate,
        storageLocation = storageLocation,
        createdAt = createdAt,
        updatedAt = updatedAt,

        // Group Domain fields into the Embeddable object
        healthInfo = HealthInfoEmbeddable(
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
    )
}