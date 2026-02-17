package com.eeseka.shelflife.user.infra.database.mappers

import com.eeseka.shelflife.user.domain.model.EmailVerificationToken
import com.eeseka.shelflife.user.infra.database.entities.EmailVerificationTokenEntity

fun EmailVerificationTokenEntity.toEmailVerificationToken(): EmailVerificationToken {
    return EmailVerificationToken(
        id = id,
        token = token,
        user = user.toUser()
    )
}