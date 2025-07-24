package com.y9vad9.brawlex.auth.domain.value

import com.y9vad9.valdi.Factory
import com.y9vad9.valdi.ValidationFailure
import com.y9vad9.valdi.builder.factory
import com.y9vad9.valdi.domain.ValueObject

@ValueObject
@JvmInline
public value class OwnershipChallengeAttempts(
    public val int: Int,
) : Comparable<OwnershipChallengeAttempts> {
    override fun compareTo(other: OwnershipChallengeAttempts): Int = int.compareTo(other.int)

    public companion object {
        public const val MIN_VALUE: Int = 0

        public val factory: Factory<Int, OwnershipChallengeAttempts, InvalidMinValue> = factory {
            constraints {
                gives(InvalidMinValue) unless { input ->
                    input >= MIN_VALUE
                }
            }

            constructor(::OwnershipChallengeAttempts)
        }
    }

    public data object InvalidMinValue : ValidationFailure
}
