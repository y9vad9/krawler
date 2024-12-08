package com.y9vad9.bcm.domain.entity.brawlstars.value

import com.y9vad9.bcm.foundation.validation.CreationFailure
import com.y9vad9.bcm.foundation.validation.SafeConstructor

@JvmInline
value class ClubTag private constructor(private val value: String) {
    companion object : SafeConstructor<ClubTag, String> {
        const val REQUIRED_SIZE = 9

        override val displayName: String = "PlayerTag"

        override fun create(value: String): Result<ClubTag> {
            val value = value.replace("#", "")

            if (value.length != REQUIRED_SIZE)
                return Result.failure(CreationFailure.ofSizeExact(REQUIRED_SIZE))

            return Result.success(ClubTag(value))
        }
    }

    override fun toString(): String {
        return "#$value"
    }
}