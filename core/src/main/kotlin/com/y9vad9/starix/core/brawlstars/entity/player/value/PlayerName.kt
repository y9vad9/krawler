package com.y9vad9.starix.core.brawlstars.entity.player.value

import com.y9vad9.starix.foundation.validation.CreationFailure
import com.y9vad9.starix.foundation.validation.ValueConstructor
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class PlayerName private constructor(val value: String) {
    companion object : ValueConstructor<PlayerName, String> {
        val SIZE_RANGE: IntRange = 1..15

        override val displayName: String = "PlayerName"

        override fun create(value: String): Result<PlayerName> {
            return when (value.length) {
                in SIZE_RANGE -> Result.success(PlayerName(value))
                else -> Result.failure(CreationFailure.ofSizeRange(SIZE_RANGE))
            }
        }
    }
}