package com.y9vad9.bcm.core.brawlstars.entity.brawler.value

import com.y9vad9.bcm.foundation.validation.ValueConstructor
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class StarPowerId private constructor(val value: Int) : Comparable<StarPowerId> {
    companion object : ValueConstructor<StarPowerId, Int> {
        override val displayName: String = "StarPowerId"

        override fun create(value: Int): Result<StarPowerId> {
            return Result.success(StarPowerId(value))
        }
    }

    override fun compareTo(other: StarPowerId): Int {
        return value.compareTo(other.value)
    }
}