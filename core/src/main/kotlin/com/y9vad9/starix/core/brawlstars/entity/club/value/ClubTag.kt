package com.y9vad9.starix.core.brawlstars.entity.club.value

import com.y9vad9.starix.foundation.validation.CreationFailure
import com.y9vad9.starix.foundation.validation.ValueConstructor
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class ClubTag private constructor(private val value: String) {
    companion object : ValueConstructor<ClubTag, String> {
        const val REQUIRED_SIZE: Int = 9

        override val displayName: String = "ClubTag"

        override fun create(value: String): Result<ClubTag> {
            val value = value.replace("#", "")

            if (value.length != REQUIRED_SIZE)
                return Result.failure(CreationFailure.ofSizeExact(REQUIRED_SIZE))

            return Result.success(ClubTag(value))
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