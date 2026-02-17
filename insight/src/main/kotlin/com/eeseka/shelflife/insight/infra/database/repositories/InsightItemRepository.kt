package com.eeseka.shelflife.insight.infra.database.repositories

import com.eeseka.shelflife.common.domain.type.InsightItemId
import com.eeseka.shelflife.common.domain.type.UserId
import com.eeseka.shelflife.insight.infra.database.entities.InsightItemEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.Instant

interface InsightItemRepository : JpaRepository<InsightItemEntity, InsightItemId> {
    fun findAllByUserId(userId: UserId): List<InsightItemEntity>
    fun findByIdAndUserId(id: InsightItemId, userId: UserId): InsightItemEntity?

    @Query(
        """
        SELECT i
        FROM InsightItemEntity i
        WHERE i.userId = :userId
        AND i.updatedAt > :since
    """
    )
    fun findUpdatedItems(userId: UserId, since: Instant): List<InsightItemEntity>
}