package com.y9vad9.starix.core.brawlstars.entity.brawler.value

import com.y9vad9.starix.foundation.validation.ValueConstructor
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class StarPowerId private constructor(val value: Int) : Comparable<com.y9vad9.starix.core.brawlstars.entity.brawler.value.StarPowerId> {
    companion object : ValueConstructor<com.y9vad9.starix.core.brawlstars.entity.brawler.value.StarPowerId, Int> {
        override val displayName: String = "StarPowerId"

        override fun create(value: Int): Result<com.y9vad9.starix.core.brawlstars.entity.brawler.value.StarPowerId> {
            return Result.success(com.y9vad9.starix.core.brawlstars.entity.brawler.value.StarPowerId(value))
        }
    }

    override fun compareTo(other: com.y9vad9.starix.core.brawlstars.entity.brawler.value.StarPowerId): Int {
        return value.compareTo(other.value)
    }
}