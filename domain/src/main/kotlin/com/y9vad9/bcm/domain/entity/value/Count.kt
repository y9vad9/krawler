package com.y9vad9.bcm.domain.entity.value

import com.y9vad9.bcm.foundation.validation.CreationFailure
import com.y9vad9.bcm.foundation.validation.SafeConstructor

@JvmInline
value class Count private constructor(val value: Int) {
    companion object : SafeConstructor<Count, Int> {
        override val displayName: String = "Count"

        override fun create(value: Int): Result<Count> {
            return when {
                value < 0 -> Result.failure(CreationFailure.ofMin(0))
                else -> Result.success(Count(value))
            }
        }
    }
}