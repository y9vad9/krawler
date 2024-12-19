package com.y9vad9.starix.core.brawlstars.entity.event.value

import com.y9vad9.starix.foundation.validation.ValueConstructor
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Trophies private constructor(val value: Int) : Comparable<Trophies> {
    companion object : ValueConstructor<Trophies, Int> {
        override val displayName: String = "Trophies"

        val ZERO = Trophies(0)

        override fun create(value: Int): Result<Trophies> {
            return Result.success(Trophies(value))
        }
    }

    operator fun plus(other: Trophies): Trophies {
        return Trophies(value + other.value)
    }

    operator fun minus(other: Trophies): Trophies {
        return Trophies(value - other.value)
    }

    override fun compareTo(other: Trophies): Int {
        return value.compareTo(other.value)
    }
}

val Trophies.isPositive: Boolean get() = value < 0
val Trophies.isNegative: Boolean get() = value < 0