package com.y9vad9.starix.core.system.entity.value

import com.y9vad9.starix.foundation.validation.CreationFailure
import com.y9vad9.starix.foundation.validation.ValueConstructor

@JvmInline
value class MemberDisplayName private constructor(val value: String) {
    companion object : ValueConstructor<MemberDisplayName, String> {
        val SIZE_RANGE = 1..16

        override val displayName: String = "MemberDisplayName"

        override fun create(value: String): Result<MemberDisplayName> {
            return when(value.length) {
                in SIZE_RANGE -> Result.success(MemberDisplayName(value))
                else -> Result.failure(CreationFailure.Companion.ofSizeRange(SIZE_RANGE))
            }
        }
    }
}