package com.y9vad9.bcm.domain.entity.telegram.value

import com.y9vad9.bcm.foundation.validation.SafeConstructor

@JvmInline
value class TelegramGroupId private constructor(val value: Long) {
    companion object : SafeConstructor<TelegramGroupId, Long> {
        override val displayName: String = "TelegramUserId"

        override fun create(value: Long): Result<TelegramGroupId> {
            return Result.success(TelegramGroupId(value))
        }
    }
}