package com.eeseka.shelflife.common.domain.events

import java.time.Instant

interface ShelfLifeEvent {
    val eventId: String
    val eventKey: String
    val occurredAt: Instant
    val exchange: String
}