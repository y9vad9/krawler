package com.y9vad9.brawlex.user.domain.value

import com.y9vad9.valdi.Factory
import com.y9vad9.valdi.ValidationFailure
import com.y9vad9.valdi.builder.factory
import com.y9vad9.valdi.domain.ValueObject

@ValueObject
@JvmInline
public value class LinkedPlayerName private constructor(public val string: String) {

    public companion object {
        public const val MIN_LENGTH: Int = 1
        public const val MAX_LENGTH: Int = 16

        public val LENGTH_RANGE: IntRange = MIN_LENGTH..MAX_LENGTH

        public val factory: Factory<String, LinkedPlayerName, NameNotWithinRangeFailure> = factory {
            constraints {
                gives(NameNotWithinRangeFailure) { input -> input.length !in LENGTH_RANGE }
            }

            constructor(::LinkedPlayerName)
        }
    }

    override fun toString(): String {
        return string
    }

    public object NameNotWithinRangeFailure : ValidationFailure
}
