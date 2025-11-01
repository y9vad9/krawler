package krawler.server.player.application

import krawler.server.player.application.PlayerTag.Companion.MAX_LENGTH
import krawler.server.player.application.PlayerTag.Companion.MIN_LENGTH
import krawler.server.player.application.PlayerTag.Companion.create
import krawler.server.player.application.PlayerTag.Companion.createOrNull
import krawler.server.player.application.PlayerTag.Companion.createOrThrow

/**
 * Represents a Brawl Stars player tag, used to uniquely identify players across the API.
 *
 * A valid player tag:
 * - May start with an optional `#` symbol.
 * - Must contain only uppercase letters A–Z and digits 0–9.
 * - Must be between [MIN_LENGTH] and [MAX_LENGTH] characters long (excluding the `#` if present).
 *
 * Use [create], [createOrNull], or [createOrThrow] to safely construct a `PlayerTag` instance.
 */
@JvmInline
value class PlayerTag private constructor(
    /** The validated tag string, as accepted by the Brawl Stars API. */
    private val rawString: String,
) {

    companion object {
        /** The minimum number of characters in a tag (excluding the optional `#`). */
        const val MIN_LENGTH: Int = 3

        /** The maximum number of characters in a tag (excluding the optional `#`). */
        const val MAX_LENGTH: Int = 16

        /** Regular expression to match allowed tag characters (A–Z, 0–9). */
        private val VALID_CHAR_REGEX = Regex("^[A-Z0-9]+$", RegexOption.IGNORE_CASE)

        /**
         * Attempts to create a [PlayerTag] from the given [input].
         *
         * Returns a [FactoryResult.Success] if valid, or one of [FactoryResult.Failure] variants if invalid.
         */
        fun create(input: String): FactoryResult {
            val trimmed = input.trim()
            if (trimmed.isEmpty()) return FactoryResult.Failure.Empty

            val withoutHash = trimmed.removePrefix("#")
            val length = withoutHash.length

            return when {
                length < MIN_LENGTH -> FactoryResult.Failure.TooShort(length)
                length > MAX_LENGTH -> FactoryResult.Failure.TooLong(length)
                !VALID_CHAR_REGEX.matches(withoutHash) ->
                    FactoryResult.Failure.InvalidCharacters(input)

                else -> FactoryResult.Success(PlayerTag(withoutHash.uppercase()))
            }
        }

        /**
         * Creates a [PlayerTag] or throws a meaningful [IllegalArgumentException] based on [FactoryResult].
         */
        fun createOrThrow(input: String): PlayerTag =
            when (val result = create(input)) {
                is FactoryResult.Success -> result.value
                is FactoryResult.Failure -> when (result) {
                    FactoryResult.Failure.Empty ->
                        throw IllegalArgumentException("Player tag cannot be empty.")

                    is FactoryResult.Failure.TooShort ->
                        throw IllegalArgumentException(
                            "Player tag is too short (${result.length} chars). " +
                                    "Must be at least $MIN_LENGTH."
                        )

                    is FactoryResult.Failure.TooLong ->
                        throw IllegalArgumentException(
                            "Player tag is too long (${result.length} chars). " +
                                    "Must be at most $MAX_LENGTH."
                        )

                    is FactoryResult.Failure.InvalidCharacters ->
                        throw IllegalArgumentException(
                            "Player tag '${result.input}' contains invalid characters. " +
                                    "Only A–Z and 0–9 are allowed."
                        )
                }
            }

        /**
         * Creates a [PlayerTag], or returns `null` if invalid.
         */
        fun createOrNull(input: String): PlayerTag? =
            when (val result = create(input)) {
                is FactoryResult.Success -> result.value
                is FactoryResult.Failure -> null
            }
    }

    /** Returns the tag string with a leading `#`. */
    val stringWithTagPrefix: String
        get() = if (rawString.startsWith("#")) rawString else "#$rawString"

    /** Returns the tag string without the leading `#`. */
    val stringWithoutTagPrefix: String
        get() = if (rawString.startsWith("#")) rawString.substring(1) else rawString

    /** Canonical representation, always prefixed with `#`. */
    override fun toString(): String = stringWithTagPrefix

    /**
     * Result type representing outcomes of [create].
     */
    sealed interface FactoryResult {
        data class Success(val value: PlayerTag) : FactoryResult
        sealed interface Failure : FactoryResult {
            data object Empty : Failure
            data class TooShort(val length: Int) : Failure
            data class TooLong(val length: Int) : Failure
            data class InvalidCharacters(val input: String) : Failure
        }
    }
}
