package com.y9vad9.bcm.domain.entity.brawlstars.value

import com.y9vad9.bcm.foundation.validation.CreationFailure
import com.y9vad9.bcm.foundation.validation.SafeConstructor

@JvmInline
value class PlayerName private constructor(val value: String) {
    companion object : SafeConstructor<PlayerName, String> {
        val SIZE_RANGE = 1..15

        override val displayName: String = "PlayerName"

        override fun create(value: String): Result<PlayerName> {
            return when(value.length) {
                in SIZE_RANGE -> Result.success(PlayerName(value))
                else -> Result.failure(CreationFailure.ofSizeRange(SIZE_RANGE))
            }
        }
    }
}