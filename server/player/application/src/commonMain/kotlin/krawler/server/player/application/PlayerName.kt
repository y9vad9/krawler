package krawler.server.player.application

import krawler.server.player.application.PlayerName.Companion.MAX_LENGTH
import krawler.server.player.application.PlayerName.Companion.MIN_LENGTH
import krawler.server.player.application.PlayerName.Companion.create
import krawler.server.player.application.PlayerName.Companion.createOrNull
import krawler.server.player.application.PlayerName.Companion.createOrThrow

/**
 * Represents a Brawl Stars player name, as shown in-game.
 *
 * A valid player name:
 * - Must be at least [MIN_LENGTH] character long.
 * - Must not exceed [MAX_LENGTH] characters.
 * - Can contain any character (including emojis, symbols, etc.), as long as the length is valid.
 *
 * Use [create], [createOrNull], or [createOrThrow] to safely construct a [PlayerName] instance.
 *
 * This type enforces validity at creation and should be treated as a type-safe representation
 * of a player name. Raw strings should not be passed around where [PlayerName] is expected.
 *
 * @property rawString The validated player name string.
 */
@JvmInline
value class PlayerName private constructor(
    val rawString: String,
) {
    companion object {
        /** The minimum allowed length of a player name. */
        const val MIN_LENGTH: Int = 1

        /** The maximum allowed length of a player name. */
        const val MAX_LENGTH: Int = 15

        /** The valid range of length for a player name. */
        val LENGTH_RANGE: IntRange = MIN_LENGTH..MAX_LENGTH

        /**
         * Attempts to create a [PlayerName] from the given [input] string.
         *
         * Returns a [FactoryResult.Success] if valid, or [FactoryResult.TooShort] / [FactoryResult.TooLong] if invalid.
         *
         * @param input The raw player name string to validate.
         * @return [FactoryResult] indicating success or type of failure.
         */
        fun create(input: String): FactoryResult =
            when {
                input.length < MIN_LENGTH -> FactoryResult.TooShort
                input.length > MAX_LENGTH -> FactoryResult.TooLong
                else -> FactoryResult.Success(PlayerName(input))
            }

        /**
         * Creates a [PlayerName] from [input] or throws an [IllegalArgumentException] if invalid.
         *
         * @param input The raw player name string.
         * @return A valid [PlayerName].
         * @throws IllegalArgumentException If [input] is too short or too long.
         */
        fun createOrThrow(input: String): PlayerName =
            when (val result = create(input)) {
                is FactoryResult.Success ->
                    result.value

                FactoryResult.TooShort ->
                    throw IllegalArgumentException("Player name must be at least $MIN_LENGTH characters: $input")

                FactoryResult.TooLong ->
                    throw IllegalArgumentException("Player name must be at most $MAX_LENGTH characters: $input")
            }

        /**
         * Creates a [PlayerName] from [input], or returns `null` if invalid.
         *
         * @param input The raw player name string.
         * @return A valid [PlayerName] or `null` if invalid.
         */
        fun createOrNull(input: String): PlayerName? =
            when (val result = create(input)) {
                is FactoryResult.Success -> result.value
                FactoryResult.TooShort, FactoryResult.TooLong -> null
            }
    }

    /**
     * Returns the raw string representation of this player name.
     */
    override fun toString(): String = rawString

    /**
     * Sealed interface representing the result of a [PlayerName] creation attempt.
     */
    sealed interface FactoryResult {
        /** Indicates a successful creation of [PlayerName]. */
        data class Success(val value: PlayerName) : FactoryResult

        /** Indicates the input name was shorter than [MIN_LENGTH]. */
        data object TooShort : FactoryResult

        /** Indicates the input name was longer than [MAX_LENGTH]. */
        data object TooLong : FactoryResult
    }
}
