package com.y9vad9.starix.core.brawlstars.entity.event

import com.y9vad9.starix.core.brawlstars.entity.event.value.EventSlotId
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ScheduledEvent(
    val startTime: Instant,
    val endTime: Instant,
    val slotId: EventSlotId,
    val event: Event,
)