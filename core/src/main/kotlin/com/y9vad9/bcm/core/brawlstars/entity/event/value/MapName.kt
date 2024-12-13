package com.y9vad9.bcm.core.brawlstars.entity.event.value

import com.y9vad9.bcm.foundation.validation.ValueConstructor
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class MapName private constructor(val value: String) {
    companion object : ValueConstructor<MapName, String> {
        override val displayName: String = "MapName"

        override fun create(value: String): Result<MapName> {
            return Result.success(MapName(value))
        }
    }
}