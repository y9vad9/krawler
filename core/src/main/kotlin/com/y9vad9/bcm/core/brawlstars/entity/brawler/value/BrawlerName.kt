package com.y9vad9.bcm.core.brawlstars.entity.brawler.value

import com.y9vad9.bcm.foundation.validation.ValueConstructor
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class BrawlerName private constructor(val value: String) {
    companion object : ValueConstructor<BrawlerName, String> {
        override val displayName: String = "BrawlerName"

        override fun create(value: String): Result<BrawlerName> {
            return Result.success(BrawlerName(value))
        }
    }
}