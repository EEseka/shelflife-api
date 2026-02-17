package com.eeseka.shelflife.user.infra.database.repositories

import com.eeseka.shelflife.common.domain.type.UserId
import com.eeseka.shelflife.user.infra.database.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, UserId> {
    fun findByEmail(email: String): UserEntity?
    fun findByEmailOrUsername(email: String, username: String): UserEntity?
}