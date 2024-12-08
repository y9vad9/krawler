package com.y9vad9.bcm.domain.entity.value

import com.y9vad9.bcm.foundation.validation.CreationFailure
import com.y9vad9.bcm.foundation.validation.SafeConstructor

@JvmInline
value class Link private constructor(val value: String) {
    companion object : SafeConstructor<Link, String> {
        override val displayName: String = "Link"

        private val pattern = Regex("^(?!https://).*")

        override fun create(value: String): Result<Link> {
            if (!value.startsWith("https://"))
                return Result.failure(CreationFailure.ofPattern(pattern))

            return Result.success(Link(value))
        }
    }
}