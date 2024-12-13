package com.y9vad9.bcm.core.common.entity.value

import com.y9vad9.bcm.foundation.validation.CreationFailure
import com.y9vad9.bcm.foundation.validation.ValueConstructor
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Count private constructor(val value: Int) {
    companion object : ValueConstructor<Count, Int> {
        override val displayName: String = "Count"

        override fun create(value: Int): Result<Count> {
            return when {
                value < 0 -> Result.failure(CreationFailure.ofMin(0))
                else -> Result.success(Count(value))
            }
        }
    }
}