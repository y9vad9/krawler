package com.y9vad9.starix.core.brawlstars.entity.club.value

import com.y9vad9.starix.foundation.validation.ValueConstructor
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class ClubDescription private constructor(val value: String) {
    companion object : ValueConstructor<ClubDescription, String> {
        override val displayName: String = "Description"

        override fun create(value: String): Result<ClubDescription> {
            return Result.success(ClubDescription(value))
        }
    }
}