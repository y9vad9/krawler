package krawler.client.auth.domain

/**
 * The number of times a player has attempted to verify ownership of their account.
 *
 * Ensures the value is non-negative and provides factory methods for safe creation.
 *
 * @property int The total count of ownership verification attempts.
 * @see create
 * @see createOrNull
 * @see createOrThrow
 */
@JvmInline
public value class OwnershipChallengeAttempts private constructor(
    public val int: Int,
) : Comparable<OwnershipChallengeAttempts> {
    override fun compareTo(other: OwnershipChallengeAttempts): Int = int.compareTo(other.int)

    public companion object {
        public const val MIN_VALUE: Int = 0
        public val ZERO: OwnershipChallengeAttempts = OwnershipChallengeAttempts(0)

        public fun create(int: Int): FactoryResult {
            return when {
                int < MIN_VALUE -> FactoryResult.InvalidMinValue
                else -> FactoryResult.Success(OwnershipChallengeAttempts(int))
            }
        }

        public fun createOrNull(int: Int): OwnershipChallengeAttempts? =
            (create(int) as? FactoryResult.Success)?.value

        public fun createOrThrow(int: Int): OwnershipChallengeAttempts {
            val result = create(int)

            require(result is FactoryResult.Success) {
                "OwnershipChallengeAttempts creation returned $result instead of success."
            }

            return result.value
        }
    }

    public sealed interface FactoryResult {
        public data object InvalidMinValue : FactoryResult
        public data class Success(public val value: OwnershipChallengeAttempts) : FactoryResult
    }
}
