package com.eeseka.shelflife.insight.domain.exception

class InvalidInsightImageException(override val message: String?) : RuntimeException(
    message ?: "Invalid insight item image data"
)