package com.eeseka.shelflife.user.service

import com.eeseka.shelflife.user.domain.model.ApiKey
import com.eeseka.shelflife.user.infra.database.entities.ApiKeyEntity
import com.eeseka.shelflife.user.infra.database.mappers.toApiKey
import com.eeseka.shelflife.user.infra.database.repositories.ApiKeyRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class ApiKeyService(
    private val apiKeyRepository: ApiKeyRepository,
    @param:Value("\${shelflife.api-key.expires-in-days}") val expiresInDays: Long
) {

    @Transactional
    fun createKey(email: String): ApiKey {
        val key = ApiKey.generateKey()

        val now = Instant.now()
        val entity = ApiKeyEntity(
            key = key,
            email = email.trim(),
            validFrom = now,
            expiresAt = now.plus(expiresInDays, ChronoUnit.DAYS) // I never do anything with this like scheduling a background job to delete when expired
        )

        return apiKeyRepository.save(entity).toApiKey()
    }

    fun isValidKey(key: String): Boolean {
        return apiKeyRepository.findByIdOrNull(key) != null
    }
}