package com.y9vad9.bcm.core.brawlstars.entity.event.value

import com.y9vad9.bcm.foundation.validation.CreationFailure
import com.y9vad9.bcm.foundation.validation.ValueConstructor
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class RankingPosition private constructor(val value: Int) : Comparable<RankingPosition> {
    companion object : ValueConstructor<RankingPosition, Int> {
        override val displayName: String = "RankingPosition"

        override fun create(value: Int): Result<RankingPosition> {
            if (value < 1) return Result.failure(CreationFailure.ofMin(1))
            return Result.success(RankingPosition(value))
        }
    }

    override fun compareTo(other: RankingPosition): Int {
        return value.compareTo(other.value)
    }
}