package krawler.auth.domain

import krawler.core.domain.ValueObject

/**
 * The number of bots generated during an ownership challenge.
 *
 * Ensures the value is within the allowed range [MIN_VALUE]..[MAX_VALUE] and provides
 * factory methods for safe creation.
 *
 * @property int The number of bots in the ownership challenge.
 * @see create
 * @see createOrNull
 * @see createOrThrow
 */
@ValueObject
@JvmInline
public value class OwnershipChallengeBotsAmount(
    public val int: Int,
) : Comparable<OwnershipChallengeBotsAmount> {

    override fun compareTo(other: OwnershipChallengeBotsAmount): Int = int.compareTo(other.int)

    public companion object {
        public const val MIN_VALUE: Int = 0
        public const val MAX_VALUE: Int = 9

        public fun create(int: Int): FactoryResult {
            return when {
                int !in MIN_VALUE..MAX_VALUE -> FactoryResult.InvalidRange
                else -> FactoryResult.Success(OwnershipChallengeBotsAmount(int))
            }
        }

        public fun createOrNull(int: Int): OwnershipChallengeBotsAmount? =
            (create(int) as? FactoryResult.Success)?.value

        public fun createOrThrow(int: Int): OwnershipChallengeBotsAmount {
            val result = create(int)
            require(result is FactoryResult.Success) {
                "OwnershipChallengeBotsAmount creation returned $result instead of success."
            }
            return result.value
        }
    }

    public sealed interface FactoryResult {
        public data object InvalidRange : FactoryResult
        public data class Success(public val value: OwnershipChallengeBotsAmount) : FactoryResult
    }
}
