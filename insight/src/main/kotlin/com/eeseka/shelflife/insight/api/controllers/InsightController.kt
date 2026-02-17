package com.eeseka.shelflife.insight.api.controllers

import com.eeseka.shelflife.common.api.util.requestUserId
import com.eeseka.shelflife.common.domain.type.InsightItemId
import com.eeseka.shelflife.insight.api.dto.InsightItemDto
import com.eeseka.shelflife.insight.api.dto.UpsertInsightItemRequest
import com.eeseka.shelflife.insight.api.mappers.toDomain
import com.eeseka.shelflife.insight.api.mappers.toDto
import com.eeseka.shelflife.insight.service.InsightService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@RequestMapping("/api/insight")
class InsightController(
    private val insightService: InsightService
) {
    @GetMapping
    fun getInsightItems(): List<InsightItemDto> {
        return insightService.getInsightItems(requestUserId).map { it.toDto() }
    }

    @GetMapping("/{itemId}")
    fun getInsightItem(
        @PathVariable itemId: InsightItemId
    ): InsightItemDto {
        return insightService.getInsightItem(itemId = itemId, userId = requestUserId).toDto()
    }

    @GetMapping("/updates")
    fun getUpdatedInsightItems(
        @RequestParam("since") since: Long
    ): List<InsightItemDto> {
        return insightService.getUpdatedInsightItems(
            userId = requestUserId,
            since = Instant.ofEpochMilli(since)
        ).map { it.toDto() }
    }

    @PostMapping
    fun createItem(
        @Valid @RequestBody body: UpsertInsightItemRequest
    ): InsightItemDto {
        val domainItem = body.toDomain(requestUserId)
        return insightService.createItem(domainItem).toDto()
    }

    @PutMapping
    fun updateItem(
        @Valid @RequestBody body: UpsertInsightItemRequest
    ): InsightItemDto {
        val domainItem = body.toDomain(requestUserId)
        return insightService.updateItem(domainItem).toDto()
    }

    @DeleteMapping("/{itemId}")
    fun deleteItem(
        @PathVariable itemId: InsightItemId
    ) {
        insightService.deleteItem(
            itemId = itemId,
            userId = requestUserId
        )
    }

    @DeleteMapping
    fun deleteAllItems() {
        insightService.deleteAllItems(requestUserId)
    }
}