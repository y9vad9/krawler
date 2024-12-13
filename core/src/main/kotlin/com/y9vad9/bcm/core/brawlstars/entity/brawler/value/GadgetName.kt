package com.y9vad9.bcm.core.brawlstars.entity.brawler.value

import com.y9vad9.bcm.foundation.validation.ValueConstructor
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class GadgetName private constructor(val value: String) {
    companion object : ValueConstructor<GadgetName, String> {
        override val displayName: String = "GadgetName"

        override fun create(value: String): Result<GadgetName> {
            return Result.success(GadgetName(value))
        }
    }
}