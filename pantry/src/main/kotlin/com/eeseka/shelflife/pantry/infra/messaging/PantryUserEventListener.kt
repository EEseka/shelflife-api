package com.eeseka.shelflife.pantry.infra.messaging

import com.eeseka.shelflife.common.domain.events.user.UserEvent
import com.eeseka.shelflife.common.infra.message_queue.MessageQueues
import com.eeseka.shelflife.pantry.service.PantryService
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class PantryUserEventListener(
    private val pantryService: PantryService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @RabbitListener(queues = [MessageQueues.PANTRY_USER_EVENTS])
    fun handleUserEvent(event: UserEvent) {
        when (event) {
            is UserEvent.Deleted -> {
                pantryService.deleteAllItems(event.userId)
                logger.info("Deleted all pantry items for user: ${event.userId}")
            }

            else -> Unit
        }
    }
}