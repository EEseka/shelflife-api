package com.eeseka.shelflife.user.infra.database.repositories

import com.eeseka.shelflife.user.infra.database.entities.ApiKeyEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ApiKeyRepository : JpaRepository<ApiKeyEntity, String>