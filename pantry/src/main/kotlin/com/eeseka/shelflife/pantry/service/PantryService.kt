package com.eeseka.shelflife.pantry.service

import com.eeseka.shelflife.common.domain.type.PantryItemId
import com.eeseka.shelflife.common.domain.type.UserId
import com.eeseka.shelflife.pantry.domain.exception.InvalidPantryImageException
import com.eeseka.shelflife.pantry.domain.exception.PantryItemNotFoundException
import com.eeseka.shelflife.pantry.domain.models.PantryImageUploadCredentials
import com.eeseka.shelflife.pantry.domain.models.PantryItem
import com.eeseka.shelflife.pantry.infra.database.mappers.toDomain
import com.eeseka.shelflife.pantry.infra.database.mappers.toEntity
import com.eeseka.shelflife.pantry.infra.database.repositories.PantryItemRepository
import com.eeseka.shelflife.pantry.infra.storage.SupabasePantryStorageService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class PantryService(
    private val pantryItemRepository: PantryItemRepository,
    private val pantryStorageService: SupabasePantryStorageService,
    @param:Value("\${supabase.url}") private val supabaseUrl: String,
) {
    private val logger = LoggerFactory.getLogger(PantryService::class.java)

    fun getPantryItems(userId: UserId): List<PantryItem> {
        return pantryItemRepository.findAllByUserId(userId).map { it.toDomain() }
    }

    fun getPantryItem(itemId: PantryItemId, userId: UserId): PantryItem {
        return pantryItemRepository.findByIdAndUserId(itemId, userId)?.toDomain()
            ?: throw PantryItemNotFoundException(itemId.toString())
    }

    @Transactional
    fun createItem(item: PantryItem): PantryItem {
        validateImageUrl(item.imageUrl)

        val savedEntity = pantryItemRepository.save(item.toEntity())
        return savedEntity.toDomain()
    }

    @Transactional
    fun updateItem(item: PantryItem): PantryItem {
        val existingEntity = pantryItemRepository.findByIdAndUserId(item.id, item.userId)
            ?: throw PantryItemNotFoundException(item.id.toString())

        validateImageUrl(item.imageUrl)

        if (existingEntity.imageUrl != null && existingEntity.imageUrl != item.imageUrl) {
            try {
                pantryStorageService.deleteFile(existingEntity.imageUrl!!)
            } catch (e: Exception) {
                // Best Practice: Log this error but DO NOT roll back the transaction.
                // It is better to have a phantom file in the cloud than to fail the user's update.
                logger.error("Failed to clean up old image for item ${item.id}", e)
            }
        }

        val savedEntity = pantryItemRepository.save(item.toEntity())
        return savedEntity.toDomain()
    }

    @Transactional
    fun deleteItem(itemId: PantryItemId, userId: UserId, deleteImage: Boolean = true) {
        val existingEntity = pantryItemRepository.findByIdAndUserId(itemId, userId)
            ?: throw PantryItemNotFoundException(itemId.toString())

        if (deleteImage) {
            existingEntity.imageUrl?.let { url ->
                try {
                    pantryStorageService.deleteFile(url)
                } catch (e: Exception) {
                    logger.warn("Failed to delete image from storage during item deletion", e)
                }
            }
        }

        pantryItemRepository.delete(existingEntity)
    }

    @Transactional
    fun deleteAllItems(userId: UserId) {
        val items = pantryItemRepository.findAllByUserId(userId)

        if (items.isNotEmpty()) {
            val imageUrls = items.mapNotNull { it.imageUrl }

            // OPTIMIZED: Chunked by 1000 (Supabase Max Limit)
            imageUrls.chunked(1000).forEach { batch ->
                try {
                    pantryStorageService.deleteManyFiles(batch)
                } catch (e: Exception) {
                    // Log but don't stop the transaction.
                    // Orphaned files are cheaper than a failed account deletion.
                    logger.error("Failed to delete batch of images for user $userId", e)
                }
            }

            pantryItemRepository.deleteAll(items)
        }
    }

    fun generateImageUploadUrl(
        userId: UserId,
        itemId: PantryItemId,
        mimeType: String
    ): PantryImageUploadCredentials {
        return pantryStorageService.generateSignedUploadUrl(userId, itemId, mimeType)
    }

    /**
     * Delta Sync: Returns only the items that have changed since the given timestamp.
     * This saves bandwidth by not re-downloading the whole pantry.
     */
    fun getUpdatedPantryItems(userId: UserId, since: Instant): List<PantryItem> {
        return pantryItemRepository.findUpdatedItems(userId, since).map { it.toDomain() }
    }

    private fun validateImageUrl(url: String?) {
        if (url != null && !url.startsWith(supabaseUrl)) {
            throw InvalidPantryImageException("Invalid image URL.")
        }
    }
}