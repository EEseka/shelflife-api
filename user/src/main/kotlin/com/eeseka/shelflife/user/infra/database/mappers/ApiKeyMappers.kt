package com.eeseka.shelflife.user.infra.database.mappers

import com.eeseka.shelflife.user.domain.model.ApiKey
import com.eeseka.shelflife.user.infra.database.entities.ApiKeyEntity

fun ApiKeyEntity.toApiKey(): ApiKey {
    return ApiKey(
        key = key,
        validFrom = validFrom,
        expiresAt = expiresAt,
    )
}