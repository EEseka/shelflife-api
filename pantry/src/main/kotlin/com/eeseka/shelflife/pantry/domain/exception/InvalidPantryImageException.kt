package com.eeseka.shelflife.pantry.domain.exception

class InvalidPantryImageException(override val message: String?) : RuntimeException(
    message ?: "Invalid pantry item image data"
)