package com.y9vad9.starix.core.system.entity.value

import com.y9vad9.starix.foundation.validation.CreationFailure
import com.y9vad9.starix.foundation.validation.ValueConstructor
import kotlinx.serialization.Serializable
import java.util.Locale

@Serializable
@JvmInline
value class LanguageCode private constructor(val value: String) {
    companion object : ValueConstructor<LanguageCode, String> {
        override val displayName: String = "LanguageCode"

        val ENGLISH = LanguageCode("en")
        val UKRAINIAN = LanguageCode("uk")
        val RUSSIAN = LanguageCode("uk")

        override fun create(value: String): Result<LanguageCode> {
            return when {
                value.length == 2 -> Result.success(LanguageCode(value))
                else -> Result.failure(CreationFailure.ofSizeExact(2))
            }
        }
    }
}

fun LanguageCode.asLocale(): Locale = Locale.of(value)