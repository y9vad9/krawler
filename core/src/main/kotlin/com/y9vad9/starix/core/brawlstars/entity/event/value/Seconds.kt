package com.y9vad9.starix.core.brawlstars.entity.event.value

import com.y9vad9.starix.foundation.validation.ValueConstructor
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Serializable
@JvmInline
value class Seconds private constructor(val value: Int) : Comparable<Seconds> {
    companion object : ValueConstructor<Seconds, Int> {
        override val displayName: String = "Seconds"

        override fun create(value: Int): Result<Seconds> {
            return Result.success(Seconds(value))
        }
    }

    override fun compareTo(other: Seconds): Int {
        return value.compareTo(other.value)
    }
}

fun Seconds.toDuration(): Duration {
    return value.seconds
}