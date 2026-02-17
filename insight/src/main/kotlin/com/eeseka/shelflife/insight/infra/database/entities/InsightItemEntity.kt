package com.eeseka.shelflife.insight.infra.database.entities

import com.eeseka.shelflife.common.domain.type.InsightItemId
import com.eeseka.shelflife.common.domain.type.UserId
import com.eeseka.shelflife.insight.domain.models.InsightStatus
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(
    name = "insight_items",
    schema = "insight_service",
    indexes = [
        Index(name = "idx_insight_item_user_id", columnList = "user_id")
    ]
)
class InsightItemEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: InsightItemId? = null,

    @Column(name = "user_id", nullable = false)
    var userId: UserId,

    @Column(nullable = false)
    var barcode: String,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = true)
    var brand: String? = null,

    // --- IMAGE ---
    @Column(nullable = true)
    var imageUrl: String? = null,

    // --- QUANTITY ---
    @Column(nullable = false)
    var quantity: Double = 1.0,

    @Column(nullable = false)
    var quantityUnit: String = "",

    // --- DATE ---
    @Column(nullable = false)
    var actionDate: LocalDate,

    // --- STATUS ---
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: InsightStatus,

    // --- HEALTH ---
    @Embedded
    var healthInfo: HealthInfoEmbeddable? = null,

    // --- METADATA ---
    @CreationTimestamp
    var createdAt: Instant = Instant.now(),

    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
)

@Embeddable
data class HealthInfoEmbeddable(
    var nutriScore: String? = null,
    var novaGroup: Int? = null,
    var ecoScore: String? = null
)