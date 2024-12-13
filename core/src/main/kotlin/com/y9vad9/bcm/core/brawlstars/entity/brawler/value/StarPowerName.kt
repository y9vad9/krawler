package com.y9vad9.bcm.core.brawlstars.entity.brawler.value

import com.y9vad9.bcm.foundation.validation.ValueConstructor
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class StarPowerName private constructor(val value: String) {
    companion object : ValueConstructor<StarPowerName, String> {
        override val displayName: String = "StarPowerName"

        override fun create(value: String): Result<StarPowerName> {
            return Result.success(StarPowerName(value))
        }
    }
}