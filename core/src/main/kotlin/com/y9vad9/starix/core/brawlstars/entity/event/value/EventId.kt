package com.y9vad9.starix.core.brawlstars.entity.event.value

import com.y9vad9.starix.foundation.validation.ValueConstructor
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class EventId private constructor(val value: Int) : Comparable<EventId> {
    companion object : ValueConstructor<EventId, Int> {
        override val displayName: String = "GearId"

        override fun create(value: Int): Result<EventId> {
            return Result.success(EventId(value))
        }
    }

    override fun compareTo(other: EventId): Int {
        return value.compareTo(other.value)
    }
}