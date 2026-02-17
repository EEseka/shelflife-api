package com.eeseka.shelflife.user.api.mappers

import com.eeseka.shelflife.user.api.dto.ApiKeyDto
import com.eeseka.shelflife.user.domain.model.ApiKey

fun ApiKey.toApiKeyDto(): ApiKeyDto {
    return ApiKeyDto(
        key = key,
        validFrom = validFrom,
        expiresAt = expiresAt,
    )
}