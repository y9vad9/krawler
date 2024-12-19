package com.y9vad9.starix.core.brawlstars.entity.brawler.value

import com.y9vad9.starix.foundation.validation.ValueConstructor
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class GearId private constructor(val value: Int) : Comparable<GearId> {
    companion object : ValueConstructor<GearId, Int> {
        override val displayName: String = "GearId"

        override fun create(value: Int): Result<GearId> {
            return Result.success(GearId(value))
        }
    }

    override fun compareTo(other: GearId): Int {
        return value.compareTo(other.value)
    }
}