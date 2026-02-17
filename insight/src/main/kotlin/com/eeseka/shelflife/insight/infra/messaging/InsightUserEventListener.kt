package com.eeseka.shelflife.insight.infra.messaging

import com.eeseka.shelflife.common.domain.events.user.UserEvent
import com.eeseka.shelflife.common.infra.message_queue.MessageQueues
import com.eeseka.shelflife.insight.service.InsightService
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class InsightUserEventListener(
    private val insightService: InsightService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @RabbitListener(queues = [MessageQueues.INSIGHT_USER_EVENTS])
    fun handleUserEvent(event: UserEvent) {
        when (event) {
            is UserEvent.Deleted -> {
                insightService.deleteAllItems(event.userId)
                logger.info("Deleted all insight items for user: ${event.userId}")
            }

            else -> Unit
        }
    }
}