package krawler.user.domain

import kotlin.jvm.JvmInline

/**
 * Represents a Brawl Stars player tag.
 *
 * A player tag uniquely identifies a player in Brawl Stars. Input tags are validated case-insensitively,
 * normalized to uppercase, and may optionally start with a `#` prefix.
 *
 * Use the factory methods ([create], [createOrNull], [createOrThrow]) to safely construct instances.
 */
@JvmInline
public value class PlayerTag private constructor(private val string: String) {

    /** The tag string with a leading `#`. Always present. */
    public val stringWithTagPrefix: String
        get() = if (string.startsWith("#")) string else "#$string"

    /** The tag string without the leading `#`. Always normalized to uppercase. */
    public val stringWithoutTagPrefix: String
        get() = if (string.startsWith("#")) string.substring(1) else string

    public companion object {
        /** Minimum number of characters allowed in a tag (excluding optional `#`). */
        public const val MIN_LENGTH: Int = 3

        /** Maximum number of characters allowed in a tag (excluding optional `#`). */
        public const val MAX_LENGTH: Int = 14

        /**
         * Attempts to create a [PlayerTag] from a string.
         *
         * @param tag Input string, may include a leading `#`.
         * @return [FactoryResult.Success] if valid, [FactoryResult.TagNotWithinRange] or
         * [FactoryResult.InvalidFormat] if invalid.
         */
        public fun create(tag: String): FactoryResult {
            val normalized = tag.removePrefix("#").uppercase()
            return when {
                normalized.length !in MIN_LENGTH..MAX_LENGTH -> FactoryResult.TagNotWithinRange
                !normalized.matches(Regex("^[A-Z0-9]{3,14}$")) -> FactoryResult.InvalidFormat
                else -> FactoryResult.Success(PlayerTag(normalized))
            }
        }

        /** Returns a valid [PlayerTag] or null if the input is invalid. */
        public fun createOrNull(tag: String): PlayerTag? =
            (create(tag) as? FactoryResult.Success)?.value

        /**
         * Returns a valid [PlayerTag] or throws [IllegalArgumentException] if invalid.
         *
         * @throws IllegalArgumentException when the input is invalid.
         */
        public fun createOrThrow(tag: String): PlayerTag {
            val result = create(tag)
            require(result is FactoryResult.Success) {
                "BrawlStarsPlayerTag creation returned $result instead of success."
            }
            return result.value
        }
    }

    /** Represents the result of creating a [PlayerTag]. */
    public sealed interface FactoryResult {
        /** The input tag was too short or too long. */
        public data object TagNotWithinRange : FactoryResult

        /** The input tag contained invalid characters. */
        public data object InvalidFormat : FactoryResult

        /** Successfully created a [PlayerTag]. */
        public data class Success(public val value: PlayerTag) : FactoryResult
    }

    /** Returns [stringWithTagPrefix] as the string representation. */
    override fun toString(): String = stringWithTagPrefix
}
