package com.y9vad9.krawler.auth.domain.value

import com.y9vad9.valdi.Factory
import com.y9vad9.valdi.ValidationFailure
import com.y9vad9.valdi.builder.factory
import com.y9vad9.valdi.domain.ValueObject

@ValueObject
@JvmInline
public value class OwnershipChallengeBotsAmount private constructor(
    public val int: Int,
) {
    public companion object {
        public const val MIN_VALUE: Int = 0
        public const val MAX_VALUE: Int = 9

        public val VALUE_RANGE: IntRange = MIN_VALUE..MAX_VALUE

        public val factory: Factory<Int, OwnershipChallengeBotsAmount, ValueNotWithinRangeFailure> = factory {
            constraints {
                gives(ValueNotWithinRangeFailure) unless { input -> input in VALUE_RANGE }
            }

            constructor(::OwnershipChallengeBotsAmount)
        }
    }

    public data object ValueNotWithinRangeFailure : ValidationFailure
}
