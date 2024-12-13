package com.y9vad9.bcm.core.brawlstars.entity.player.value

import com.y9vad9.bcm.foundation.validation.ValueConstructor
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class IconId private constructor(val value: Int) : Comparable<IconId> {
    companion object : ValueConstructor<IconId, Int> {
        override val displayName: String = "IconId"

        override fun create(value: Int): Result<IconId> {
            return Result.success(IconId(value))
        }
    }

    override fun compareTo(other: IconId): Int {
        return value.compareTo(other.value)
    }
}