package com.phpusr.wildrace.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.phpusr.wildrace.domain.dto.EventType
import com.phpusr.wildrace.domain.dto.ObjectType
import com.phpusr.wildrace.domain.dto.WsEvenDto
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

@Component
class WsSender(
        private val template: SimpMessagingTemplate,
        private val mapper: ObjectMapper
) {
    fun <T> getSender(objectType: ObjectType, view: Class<*>): (EventType, T) -> Unit {
        val writer = mapper.setConfig(mapper.serializationConfig).writerWithView(view)
        return { eventType: EventType, payload: T ->
            val value = writer.writeValueAsString(payload)
            template.convertAndSend("/topic/activity", WsEvenDto(objectType, eventType, value))
        }
    }
}