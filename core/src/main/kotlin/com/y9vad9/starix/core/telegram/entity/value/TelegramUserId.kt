package com.y9vad9.starix.core.telegram.entity.value

import com.y9vad9.starix.foundation.validation.ValueConstructor
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class TelegramUserId private constructor(val value: Long) {
    companion object : ValueConstructor<TelegramUserId, Long> {
        override val displayName: String = "TelegramUserId"

        override fun create(value: Long): Result<TelegramUserId> {
            return Result.success(TelegramUserId(value))
        }
    }
}