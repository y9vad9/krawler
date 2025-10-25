package krawler.client.auth.domain

/**
 * Represents a Brawl Stars player tag linked to a user within the system.
 *
 * A player tag uniquely identifies a player in Brawl Stars. It consists of alphanumeric
 * characters, with a length between [MIN_LENGTH] and [MAX_LENGTH], and may optionally
 * start with the `#` prefix.
 *
 * Input tags are validated case-insensitively and normalized to uppercase internally.
 *
 * Validation rules:
 * - The tag must have between [MIN_LENGTH] and [MAX_LENGTH] characters, excluding the optional `#`.
 * - Only letters A–Z and digits 0–9 are allowed.
 *
 * Use the factory methods ([create], [createOrNull], [createOrThrow]) to safely create instances.
 *
 * Access the tag as:
 * - [stringWithTagPrefix]: always with a leading `#`.
 * - [stringWithoutTagPrefix]: always without the leading `#`.
 *
 * See also:
 * - [Supercell Player Tag Info](https://support.supercell.com/brawl-stars/en/articles/player-tag.html)
 *
 * @param string The normalized tag string (always uppercase, may include `#` prefix).
 */
@JvmInline
public value class ChallengedBrawlStarsPlayerTag private constructor(private val string: String) {

    public val stringWithTagPrefix: String get() = if (string.startsWith("#")) string else "#$string"
    public val stringWithoutTagPrefix: String get() = if (string.startsWith("#")) string.substring(1) else string

    public companion object {
        public const val MIN_LENGTH: Int = 3
        public const val MAX_LENGTH: Int = 14
        private val LENGTH_RANGE: IntRange = MIN_LENGTH..MAX_LENGTH
        private val REGEX: Regex = Regex(
            pattern = "^[A-Z0-9]{$MIN_LENGTH,$MAX_LENGTH}$",
            option = RegexOption.IGNORE_CASE
        )

        public fun create(tag: String): FactoryResult {
            val normalized = tag.removePrefix("#").uppercase()
            return when {
                normalized.length !in LENGTH_RANGE -> FactoryResult.TagNotWithinRange
                !normalized.matches(REGEX) -> FactoryResult.InvalidFormat
                else -> FactoryResult.Success(ChallengedBrawlStarsPlayerTag(normalized))
            }
        }

        public fun createOrNull(tag: String): ChallengedBrawlStarsPlayerTag? =
            (create(tag) as? FactoryResult.Success)?.value

        public fun createOrThrow(tag: String): ChallengedBrawlStarsPlayerTag {
            val result = create(tag)
            require(result is FactoryResult.Success) {
                "ChallengedBrawlStarsPlayerTag creation returned $result instead of success."
            }
            return result.value
        }
    }

    public sealed interface FactoryResult {
        public data object TagNotWithinRange : FactoryResult
        public data object InvalidFormat : FactoryResult
        public data class Success(public val value: ChallengedBrawlStarsPlayerTag) : FactoryResult
    }

    override fun toString(): String = stringWithTagPrefix
}
