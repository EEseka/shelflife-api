package com.eeseka.shelflife.pantry.api.controllers

import com.eeseka.shelflife.common.api.util.requestUserId
import com.eeseka.shelflife.common.domain.type.PantryItemId
import com.eeseka.shelflife.pantry.api.dto.GenerateImageUploadUrlRequest
import com.eeseka.shelflife.pantry.api.dto.PantryImageUploadResponse
import com.eeseka.shelflife.pantry.api.dto.PantryItemDto
import com.eeseka.shelflife.pantry.api.dto.UpsertPantryItemRequest
import com.eeseka.shelflife.pantry.api.mappers.toDomain
import com.eeseka.shelflife.pantry.api.mappers.toDto
import com.eeseka.shelflife.pantry.api.mappers.toResponse
import com.eeseka.shelflife.pantry.service.PantryService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@RequestMapping("/api/pantry")
class PantryController(
    private val pantryService: PantryService
) {
    @GetMapping
    fun getPantryItems(): List<PantryItemDto> {
        return pantryService.getPantryItems(requestUserId).map { it.toDto() }
    }

    @GetMapping("/{itemId}")
    fun getPantryItem(
        @PathVariable itemId: PantryItemId
    ): PantryItemDto {
        return pantryService.getPantryItem(itemId = itemId, userId = requestUserId).toDto()
    }

    @GetMapping("/updates")
    fun getUpdatedPantryItems(
        @RequestParam("since") since: Long
    ): List<PantryItemDto> {
        return pantryService.getUpdatedPantryItems(
            userId = requestUserId,
            since = Instant.ofEpochMilli(since)
        ).map { it.toDto() }
    }

    @PostMapping
    fun createItem(
        @Valid @RequestBody body: UpsertPantryItemRequest
    ): PantryItemDto {
        val domainItem = body.toDomain(requestUserId)
        return pantryService.createItem(domainItem).toDto()
    }

    @PutMapping
    fun updateItem(
        @Valid @RequestBody body: UpsertPantryItemRequest
    ): PantryItemDto {
        val domainItem = body.toDomain(requestUserId)
        return pantryService.updateItem(domainItem).toDto()
    }

    @DeleteMapping("/{itemId}")
    fun deleteItem(
        @PathVariable itemId: PantryItemId,
        @RequestParam(required = false, defaultValue = "true") deleteImage: Boolean
    ) {
        pantryService.deleteItem(
            itemId = itemId,
            userId = requestUserId,
            deleteImage = deleteImage
        )
    }

    @PostMapping("/pantry-image-upload")
    fun generateImageUploadUrl(
        @Valid @RequestBody body: GenerateImageUploadUrlRequest
    ): PantryImageUploadResponse {
        return pantryService.generateImageUploadUrl(
            userId = requestUserId,
            itemId = body.itemId,
            mimeType = body.mimeType
        ).toResponse()
    }
}