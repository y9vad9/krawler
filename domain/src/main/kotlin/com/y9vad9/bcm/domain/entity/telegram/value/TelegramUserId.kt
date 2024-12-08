package com.y9vad9.bcm.domain.entity.telegram.value

import com.y9vad9.bcm.foundation.validation.SafeConstructor

@JvmInline
value class TelegramUserId private constructor(val value: Long) {
    companion object : SafeConstructor<TelegramUserId, Long> {
        override val displayName: String = "TelegramUserId"

        override fun create(value: Long): Result<TelegramUserId> {
            return Result.success(TelegramUserId(value))
        }
    }
}