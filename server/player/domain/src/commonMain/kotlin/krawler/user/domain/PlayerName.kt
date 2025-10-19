package krawler.user.domain

/**
 * Represents a Brawl Stars player name.
 *
 * The name must have a length between [MIN_LENGTH] and [MAX_LENGTH] characters.
 *
 * Use the factory methods ([create], [createOrNull], [createOrThrow]) to safely/unsafely create instances.
 *
 * @property string The player's name.
 */
@JvmInline
public value class PlayerName private constructor(public val string: String) {

    public companion object {
        public const val MIN_LENGTH: Int = 1
        public const val MAX_LENGTH: Int = 16
        private val LENGTH_RANGE: IntRange = MIN_LENGTH..MAX_LENGTH

        public fun create(name: String): FactoryResult =
            if (name.length !in LENGTH_RANGE) {
                FactoryResult.NameNotWithinRange
            } else {
                FactoryResult.Success(PlayerName(name))
            }

        public fun createOrNull(name: String): PlayerName? =
            (create(name) as? FactoryResult.Success)?.value

        public fun createOrThrow(name: String): PlayerName {
            val result = create(name)
            require(result is FactoryResult.Success) {
                "PlayerName creation returned $result instead of success."
            }
            return result.value
        }
    }

    public sealed interface FactoryResult {
        public data object NameNotWithinRange : FactoryResult
        public data class Success(public val value: PlayerName) : FactoryResult
    }

    override fun toString(): String = string
}

