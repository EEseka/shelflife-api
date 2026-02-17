package com.eeseka.shelflife.common.api.util

import com.eeseka.shelflife.common.domain.exception.UnauthorizedException
import com.eeseka.shelflife.common.domain.type.UserId
import org.springframework.security.core.context.SecurityContextHolder

val requestUserId: UserId
    get() = SecurityContextHolder.getContext().authentication?.principal as? UserId
        ?: throw UnauthorizedException()