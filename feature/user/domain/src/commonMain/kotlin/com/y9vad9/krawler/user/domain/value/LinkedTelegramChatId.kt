package com.y9vad9.krawler.user.domain.value

import com.y9vad9.valdi.Factory
import com.y9vad9.valdi.ValidationFailure
import com.y9vad9.valdi.builder.factory
import com.y9vad9.valdi.domain.ValueObject

@JvmInline
@ValueObject
public value class LinkedTelegramChatId private constructor(public val long: Long) {
    public companion object {
        public const val MIN_VALUE: Long = 0L

        public val factory: Factory<Long, LinkedTelegramChatId, ValueLessThanRequiredMinimum> = factory {
            constraints {
                gives(ValueLessThanRequiredMinimum) unless { input -> input >= MIN_VALUE }
            }

            constructor(::LinkedTelegramChatId)
        }
    }

    public data object ValueLessThanRequiredMinimum : ValidationFailure
}
