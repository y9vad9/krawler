package com.y9vad9.krawler.user.domain.value

import com.y9vad9.valdi.Factory
import com.y9vad9.valdi.ValidationFailure
import com.y9vad9.valdi.builder.factory
import com.y9vad9.valdi.domain.ValueObject

@ValueObject
@JvmInline
public value class BrawlStarsPlayerName private constructor(public val string: String) {

    public companion object Companion {
        public const val MIN_LENGTH: Int = 1
        public const val MAX_LENGTH: Int = 16

        public val LENGTH_RANGE: IntRange = MIN_LENGTH..MAX_LENGTH

        public val factory: Factory<String, BrawlStarsPlayerName, NameNotWithinRangeFailure> = factory {
            constraints {
                gives(NameNotWithinRangeFailure) unless { input -> input.length in LENGTH_RANGE }
            }

            constructor(::BrawlStarsPlayerName)
        }
    }

    override fun toString(): String = string

    public object NameNotWithinRangeFailure : ValidationFailure
}
