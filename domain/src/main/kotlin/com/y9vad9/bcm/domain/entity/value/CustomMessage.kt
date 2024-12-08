package com.y9vad9.bcm.domain.entity.value

import com.y9vad9.bcm.foundation.validation.CreationFailure
import com.y9vad9.bcm.foundation.validation.SafeConstructor

@JvmInline
value class CustomMessage private constructor(val value: String) {
    companion object : SafeConstructor<CustomMessage, String> {
        override val displayName: String = "JoinRequestMessage"

        val SIZE_RANGE = 0..2000

        override fun create(value: String): Result<CustomMessage> {
            return when(value.length) {
                in SIZE_RANGE -> Result.success(CustomMessage(value))
                else -> Result.failure(CreationFailure.ofSizeRange(SIZE_RANGE))
            }
        }
    }
}