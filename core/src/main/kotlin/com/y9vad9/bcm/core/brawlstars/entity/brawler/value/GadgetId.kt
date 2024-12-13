package com.y9vad9.bcm.core.brawlstars.entity.brawler.value

import com.y9vad9.bcm.foundation.validation.ValueConstructor
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class GadgetId private constructor(val value: Int) : Comparable<GadgetId> {
    companion object : ValueConstructor<GadgetId, Int> {
        override val displayName: String = "GearId"

        override fun create(value: Int): Result<GadgetId> {
            return Result.success(GadgetId(value))
        }
    }

    override fun compareTo(other: GadgetId): Int {
        return value.compareTo(other.value)
    }
}