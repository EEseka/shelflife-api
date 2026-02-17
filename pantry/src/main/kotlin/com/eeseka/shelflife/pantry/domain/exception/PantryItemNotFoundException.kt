package com.eeseka.shelflife.pantry.domain.exception

class PantryItemNotFoundException(id: String) : RuntimeException("Pantry item with ID $id not found")