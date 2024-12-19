package com.y9vad9.starix.core.brawlstars.entity.event

import com.y9vad9.bcm.core.brawlstars.entity.event.value.EventId
import com.y9vad9.bcm.core.brawlstars.entity.event.value.EventMode
import com.y9vad9.bcm.core.brawlstars.entity.event.value.EventModifier
import com.y9vad9.bcm.core.brawlstars.entity.event.value.MapName
import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val mode: EventMode,
    val id: EventId,
    val map: MapName,
    val modifiers: List<EventModifier>? = null,
)

val Event.isWithoutModifiers: Boolean
    get() = modifiers.isNullOrEmpty() || modifiers.singleOrNull() == EventModifier.NONE
