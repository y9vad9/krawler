package com.y9vad9.bcm.core.common.entity.value

import com.y9vad9.bcm.foundation.validation.CreationFailure
import com.y9vad9.bcm.foundation.validation.ValueConstructor

@JvmInline
value class Link private constructor(val value: String) {
    companion object : ValueConstructor<Link, String> {
        override val displayName: String = "Link"

        private val pattern = Regex("^(?!https://).*")

        override fun create(value: String): Result<Link> {
            if (!value.startsWith("https://"))
                return Result.failure(CreationFailure.ofPattern(pattern))

            return Result.success(Link(value))
        }
    }
}