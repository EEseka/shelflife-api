package com.eeseka.shelflife.insight.service

import com.eeseka.shelflife.common.domain.type.InsightItemId
import com.eeseka.shelflife.common.domain.type.UserId
import com.eeseka.shelflife.insight.domain.exception.InsightItemNotFoundException
import com.eeseka.shelflife.insight.domain.exception.InvalidInsightImageException
import com.eeseka.shelflife.insight.domain.models.InsightItem
import com.eeseka.shelflife.insight.infra.database.mappers.toDomain
import com.eeseka.shelflife.insight.infra.database.mappers.toEntity
import com.eeseka.shelflife.insight.infra.database.repositories.InsightItemRepository
import com.eeseka.shelflife.insight.infra.storage.SupabaseInsightStorageService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class InsightService(
    private val insightItemRepository: InsightItemRepository,
    private val insightStorageService: SupabaseInsightStorageService,
    @param:Value("\${supabase.url}") private val supabaseUrl: String,
) {
    private val logger = LoggerFactory.getLogger(InsightService::class.java)

    fun getInsightItems(userId: UserId): List<InsightItem> {
        return insightItemRepository.findAllByUserId(userId).map { it.toDomain() }
    }

    fun getInsightItem(itemId: InsightItemId, userId: UserId): InsightItem {
        return insightItemRepository.findByIdAndUserId(itemId, userId)?.toDomain()
            ?: throw InsightItemNotFoundException(itemId.toString())
    }

    @Transactional
    fun createItem(item: InsightItem): InsightItem {
        validateImageUrl(item.imageUrl)

        val savedEntity = insightItemRepository.save(item.toEntity())
        return savedEntity.toDomain()
    }

    @Transactional
    fun updateItem(item: InsightItem): InsightItem {
        val existingEntity = insightItemRepository.findByIdAndUserId(item.id, item.userId)
            ?: throw InsightItemNotFoundException(item.id.toString())

        validateImageUrl(item.imageUrl)

        // If a user somehow edited the history item and changed the image (rare), clean up the old one.
        if (existingEntity.imageUrl != null && existingEntity.imageUrl != item.imageUrl) {
            try {
                insightStorageService.deleteFile(existingEntity.imageUrl!!)
            } catch (e: Exception) {
                // Best Practice: Log this error but DO NOT roll back the transaction.
                // It is better to have a phantom file in the cloud than to fail the user's update.
                logger.error("Failed to clean up old image for item ${item.id}", e)
            }
        }

        val savedEntity = insightItemRepository.save(item.toEntity())
        return savedEntity.toDomain()
    }

    @Transactional
    fun deleteItem(itemId: InsightItemId, userId: UserId) {
        val existingEntity = insightItemRepository.findByIdAndUserId(itemId, userId)
            ?: throw InsightItemNotFoundException(itemId.toString())

        existingEntity.imageUrl?.let { url ->
            try {
                insightStorageService.deleteFile(url)
            } catch (e: Exception) {
                logger.warn("Failed to delete image from storage during item deletion", e)
            }
        }

        insightItemRepository.delete(existingEntity)
    }

    @Transactional
    fun deleteAllItems(userId: UserId) {
        val items = insightItemRepository.findAllByUserId(userId)

        if (items.isNotEmpty()) {
            val imageUrls = items.mapNotNull { it.imageUrl }

            // OPTIMIZED: Chunked by 1000 (Supabase Max Limit)
            imageUrls.chunked(1000).forEach { batch ->
                try {
                    insightStorageService.deleteManyFiles(batch)
                } catch (e: Exception) {
                    // Log but don't stop the transaction.
                    // Orphaned files are cheaper than a failed account deletion.
                    logger.error("Failed to delete batch of images for user $userId", e)
                }
            }

            insightItemRepository.deleteAll(items)
        }
    }

    /**
     * Delta Sync: Returns only the items that have changed since the given timestamp.
     * This saves bandwidth by not re-downloading the whole insight.
     */
    fun getUpdatedInsightItems(userId: UserId, since: Instant): List<InsightItem> {
        return insightItemRepository.findUpdatedItems(userId, since).map { it.toDomain() }
    }

    private fun validateImageUrl(url: String?) {
        if (url != null && !url.startsWith(supabaseUrl)) {
            throw InvalidInsightImageException("Invalid image URL.")
        }
    }
}