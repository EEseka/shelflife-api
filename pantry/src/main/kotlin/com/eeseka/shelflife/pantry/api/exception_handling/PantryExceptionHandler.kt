package com.eeseka.shelflife.pantry.api.exception_handling

import com.eeseka.shelflife.pantry.domain.exception.InvalidPantryImageException
import com.eeseka.shelflife.pantry.domain.exception.PantryItemNotFoundException
import com.eeseka.shelflife.pantry.domain.exception.StorageException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class PantryExceptionHandler {

    @ExceptionHandler(PantryItemNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun onPantryItemNotFound(e: PantryItemNotFoundException) = mapOf(
        "code" to "PANTRY_ITEM_NOT_FOUND",
        "message" to e.message
    )

    @ExceptionHandler(InvalidPantryImageException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun onInvalidPantryImage(e: InvalidPantryImageException) = mapOf(
        "code" to "INVALID_PANTRY_IMAGE",
        "message" to e.message
    )

    @ExceptionHandler(StorageException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun onStorageError(e: StorageException) = mapOf(
        "code" to "STORAGE_ERROR",
        "message" to e.message
    )
}