package com.eeseka.shelflife.pantry.infra.database.repositories

import com.eeseka.shelflife.common.domain.type.PantryItemId
import com.eeseka.shelflife.common.domain.type.UserId
import com.eeseka.shelflife.pantry.infra.database.entities.PantryItemEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.Instant

interface PantryItemRepository : JpaRepository<PantryItemEntity, PantryItemId> {
    fun findAllByUserId(userId: UserId): List<PantryItemEntity>
    fun findByIdAndUserId(id: PantryItemId, userId: UserId): PantryItemEntity?

    @Query(
        """
        SELECT p
        FROM PantryItemEntity p
        WHERE p.userId = :userId
        AND p.updatedAt > :since
    """
    )
    fun findUpdatedItems(userId: UserId, since: Instant): List<PantryItemEntity>
}