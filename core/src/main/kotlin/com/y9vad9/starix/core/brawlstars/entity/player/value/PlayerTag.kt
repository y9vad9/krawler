package com.y9vad9.starix.core.brawlstars.entity.player.value

import com.y9vad9.starix.foundation.validation.CreationFailure
import com.y9vad9.starix.foundation.validation.ValueConstructor
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class PlayerTag private constructor(private val value: String) {
    companion object : ValueConstructor<PlayerTag, String> {
        const val REQUIRED_SIZE: Int = 9

        override val displayName: String = "PlayerTag"

        override fun create(value: String): Result<PlayerTag> {
            val value = value.replace("#", "")

            if (value.length != REQUIRED_SIZE)
                return Result.failure(CreationFailure.ofSizeExact(REQUIRED_SIZE))

            return Result.success(PlayerTag(value))
        }
    }


    override fun toString(): String {
        return value.let {
            if (it.contains("#"))
                return it
            else "#$it"
        }
    }
}