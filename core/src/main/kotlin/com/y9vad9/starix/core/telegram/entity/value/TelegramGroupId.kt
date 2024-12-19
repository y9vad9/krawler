package com.y9vad9.starix.core.telegram.entity.value

import com.y9vad9.starix.foundation.validation.ValueConstructor
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class TelegramGroupId private constructor(val value: Long) {
    companion object : ValueConstructor<TelegramGroupId, Long> {
        override val displayName: String = "TelegramUserId"

        override fun create(value: Long): Result<TelegramGroupId> {
            return Result.success(TelegramGroupId(value))
        }
    }
}