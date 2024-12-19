package com.y9vad9.starix.core.brawlstars.entity.event.value

import com.y9vad9.starix.foundation.validation.ValueConstructor
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class EventSlotId private constructor(val value: Int) : Comparable<EventSlotId> {
    companion object : ValueConstructor<EventSlotId, Int> {
        override val displayName: String = "EventSlotId"

        override fun create(value: Int): Result<EventSlotId> {
            return Result.success(EventSlotId(value))
        }
    }

    override fun compareTo(other: EventSlotId): Int {
        return value.compareTo(other.value)
    }
}