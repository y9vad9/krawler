package com.y9vad9.bcm.core.brawlstars.entity.brawler.value

import com.y9vad9.bcm.foundation.validation.CreationFailure
import com.y9vad9.bcm.foundation.validation.ValueConstructor
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class PowerLevel private constructor(private val value: Int) : Comparable<PowerLevel> {
    companion object : ValueConstructor<PowerLevel, Int> {
        override val displayName: String = "PowerLevel"

        val VALUE_RANGE: IntRange = 1..11

        override fun create(value: Int): Result<PowerLevel> {
            if (value !in VALUE_RANGE) return Result.failure(CreationFailure.ofSizeRange(VALUE_RANGE))
            return Result.success(PowerLevel(value))
        }
    }


    override fun toString(): String {
        return "#$value"
    }

    override fun compareTo(other: PowerLevel): Int {
        return value.compareTo(other.value)
    }
}