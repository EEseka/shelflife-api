package com.eeseka.shelflife.insight.api.exception_handling

import com.eeseka.shelflife.insight.domain.exception.InsightItemNotFoundException
import com.eeseka.shelflife.insight.domain.exception.InvalidInsightImageException
import com.eeseka.shelflife.insight.domain.exception.StorageException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class InsightExceptionHandler {

    @ExceptionHandler(InsightItemNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun onInsightItemNotFound(e: InsightItemNotFoundException) = mapOf(
        "code" to "INSIGHT_ITEM_NOT_FOUND",
        "message" to e.message
    )

    @ExceptionHandler(InvalidInsightImageException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun onInvalidInsightImage(e: InvalidInsightImageException) = mapOf(
        "code" to "INVALID_INSIGHT_IMAGE",
        "message" to e.message
    )

    @ExceptionHandler(StorageException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun onStorageError(e: StorageException) = mapOf(
        "code" to "STORAGE_ERROR",
        "message" to e.message
    )
}