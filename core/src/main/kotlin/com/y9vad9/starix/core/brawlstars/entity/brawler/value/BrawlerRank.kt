package com.y9vad9.starix.core.brawlstars.entity.brawler.value

import com.y9vad9.starix.foundation.validation.CreationFailure
import com.y9vad9.starix.foundation.validation.ValueConstructor
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class BrawlerRank private constructor(private val value: Int) : Comparable<BrawlerRank> {
    companion object : ValueConstructor<BrawlerRank, Int> {
        override val displayName: String = "BrawlerRank"

        private val VALUE_RANGE: IntRange = 1..51

        val MAX: BrawlerRank = BrawlerRank(51)

        override fun create(value: Int): Result<BrawlerRank> {
            if (value !in VALUE_RANGE) return Result.failure(CreationFailure.ofSizeRange(VALUE_RANGE))
            return Result.success(BrawlerRank(value))
        }
    }


    override fun toString(): String {
        return "#$value"
    }

    override fun compareTo(other: BrawlerRank): Int {
        return value.compareTo(other.value)
    }
}