package com.eeseka.shelflife.pantry.infra.database.entities

import com.eeseka.shelflife.common.domain.type.PantryItemId
import com.eeseka.shelflife.common.domain.type.UserId
import com.eeseka.shelflife.pantry.domain.models.StorageLocation
import com.eeseka.shelflife.pantry.infra.database.converters.StringListConverter
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(
    name = "pantry_items",
    schema = "pantry_service",
    indexes = [
        // Answers efficiently: "Show me all items for User X"
        Index(name = "idx_pantry_item_user_id", columnList = "user_id"),
        // Answers efficiently: "Show me items expiring soon for User X"
        Index(name = "idx_pantry_item_user_expiry", columnList = "user_id, expiry_date")
    ]
)
class PantryItemEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: PantryItemId? = null,

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

    @Column(nullable = true)
    var packagingSize: String? = null,

    // --- DATES ---
    @Column(nullable = false)
    var expiryDate: LocalDate,

    @Column(nullable = false)
    var purchaseDate: LocalDate,

    @Column(nullable = true)
    var openDate: LocalDate? = null,

    // --- STORAGE ---
    @Enumerated(EnumType.STRING)
    @Column(name = "storage_location", nullable = false)
    var storageLocation: StorageLocation,

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
    var ecoScore: String? = null,

    @Convert(converter = StringListConverter::class)
    @Column(columnDefinition = "TEXT") // Good practice for lists stored as strings to avoid length limits
    var allergens: List<String> = emptyList(),

    @Convert(converter = StringListConverter::class)
    @Column(columnDefinition = "TEXT")
    var labels: List<String> = emptyList(),

    var caloriesPer100g: Int? = null,
    var sugarPer100g: Double? = null,
    var fatPer100g: Double? = null,
    var proteinPer100g: Double? = null
)