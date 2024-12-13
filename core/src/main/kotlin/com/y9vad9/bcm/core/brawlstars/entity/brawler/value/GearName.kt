package com.y9vad9.bcm.core.brawlstars.entity.brawler.value

import com.y9vad9.bcm.foundation.validation.ValueConstructor
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class GearName private constructor(val value: String) {
    companion object : ValueConstructor<GearName, String> {
        override val displayName: String = "GearName"

        override fun create(value: String): Result<GearName> {
            return Result.success(GearName(value))
        }
    }
}