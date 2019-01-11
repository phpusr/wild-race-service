package com.phpusr.wildrace.domain.dto

import com.fasterxml.jackson.annotation.JsonRawValue
import com.fasterxml.jackson.annotation.JsonView
import com.phpusr.wildrace.domain.Views

@JsonView(Views.Id::class)
class WsEvenDto(
        val objectType: ObjectType,
        val eventType: EventType,
        @JsonRawValue
        val body: String
)