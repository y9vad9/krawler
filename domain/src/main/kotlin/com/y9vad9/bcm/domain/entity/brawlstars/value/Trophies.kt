package com.y9vad9.bcm.domain.entity.brawlstars.value

import com.y9vad9.bcm.foundation.validation.SafeConstructor

@JvmInline
value class Trophies private constructor(val value: Int) : Comparable<Trophies> {
    companion object : SafeConstructor<Trophies, Int> {
        override val displayName: String = "brawlstars.Trophies"

        override fun create(value: Int): Result<Trophies> {
            return Result.success(Trophies(value))
        }
    }

    override fun compareTo(other: Trophies): Int {
        return value.compareTo(other.value)
    }
}