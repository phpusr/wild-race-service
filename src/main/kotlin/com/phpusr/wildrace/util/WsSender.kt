package com.phpusr.wildrace.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.phpusr.wildrace.domain.Views
import com.phpusr.wildrace.dto.EventType
import com.phpusr.wildrace.dto.ObjectType
import com.phpusr.wildrace.dto.PostDto
import com.phpusr.wildrace.dto.WsEvenDto
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import java.util.function.BiConsumer

@Component
class WsSender(
        private val template: SimpMessagingTemplate,
        private val mapper: ObjectMapper
) {
    fun <T> getSender(objectType: ObjectType, view: Class<*>): BiConsumer<EventType, T> {
        val writer = mapper.setConfig(mapper.serializationConfig).writerWithView(view)
        return BiConsumer { eventType: EventType, payload: T ->
            val value = writer.writeValueAsString(payload)
            template.convertAndSend("/topic/activity", WsEvenDto(objectType, eventType, value))
        }
    }
}

@Configuration
class WsSenderConfiguration {
    @Bean
    fun postSender(wsSender: WsSender): BiConsumer<EventType, PostDto> {
        return wsSender.getSender(ObjectType.Post, Views.PostDtoREST::class.java)
    }

    @Bean
    fun statSender(wsSender: WsSender): BiConsumer<EventType, Map<String, Any>> {
        return wsSender.getSender(ObjectType.Stat, Views.Id::class.java)
    }
}