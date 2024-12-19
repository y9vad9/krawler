package com.y9vad9.starix.core.brawlstars.entity.club.value

import com.y9vad9.starix.foundation.validation.CreationFailure
import com.y9vad9.starix.foundation.validation.ValueConstructor
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class ClubName private constructor(val value: String) {
    companion object : ValueConstructor<ClubName, String> {
        val SIZE_RANGE: IntRange = 1..15

        override val displayName: String = "ClubName"

        override fun create(value: String): Result<ClubName> {
            return when (value.length) {
                in SIZE_RANGE -> Result.success(ClubName(value))
                else -> Result.failure(CreationFailure.ofSizeRange(SIZE_RANGE))
            }
        }
    }
}