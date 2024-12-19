package com.y9vad9.starix.core.common.entity.value

import com.y9vad9.starix.foundation.validation.CreationFailure
import com.y9vad9.starix.foundation.validation.ValueConstructor
import kotlinx.serialization.Serializable

@Serializable
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