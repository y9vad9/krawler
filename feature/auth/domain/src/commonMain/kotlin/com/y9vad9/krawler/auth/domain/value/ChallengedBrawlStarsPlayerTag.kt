package com.y9vad9.krawler.auth.domain.value

import com.y9vad9.valdi.Factory
import com.y9vad9.valdi.ValidationFailure
import com.y9vad9.valdi.builder.factory
import com.y9vad9.valdi.domain.ValueObject
import kotlin.jvm.JvmInline

/**
 * Represents a Brawl Stars player tag linked to a user within the system.
 *
 * A player tag uniquely identifies a player in Brawl Stars. It consists of alphanumeric characters,
 * with a length between [MIN_LENGTH] and [MAX_LENGTH], and may optionally start with the `#` prefix.
 *
 * This value object accepts input tags both with and without the leading `#`. Input is validated
 * case-insensitively and normalized to uppercase internally.
 *
 * Validation rules:
 * - The tag must have between [MIN_LENGTH] and [MAX_LENGTH] characters, excluding the optional `#`.
 * - Only letters A–Z and digits 0–9 are allowed.
 *
 * Use [factory] to create validated instances, handling failures via [CreationFailure].
 *
 * Access the tag as:
 * - [stringWithTagPrefix]: always with a leading `#`.
 * - [stringWithoutTagPrefix]: always without the leading `#`.
 *
 * See also:
 * - [Supercell Player Tag Info](https://support.supercell.com/brawl-stars/en/articles/player-tag.html)
 */
@ValueObject
@JvmInline
public value class ChallengedBrawlStarsPlayerTag private constructor(private val string: String) {
    public companion object Companion {
        public const val MIN_LENGTH: Int = 3
        public const val MAX_LENGTH: Int = 14

        public val LENGTH_RANGE: IntRange = MIN_LENGTH..MAX_LENGTH
        public val REGEX: Regex = Regex(
            pattern = "^#?[A-Z0-9]{3,14}$",
            option = RegexOption.IGNORE_CASE,
        )

        public val factory: Factory<String, ChallengedBrawlStarsPlayerTag, CreationFailure> = factory {
            constraints {
                gives(CreationFailure.TagNotWithinRangeFailure) unless { input ->
                    input.removePrefix("#").length in LENGTH_RANGE
                }
                gives(CreationFailure.InvalidFormatFailure) unless { input -> input.matches(REGEX) }
            }

            constructor { ChallengedBrawlStarsPlayerTag(it.uppercase()) }
        }
    }

    public val stringWithTagPrefix: String get() = if (string.startsWith("#")) string else "#$string"
    public val stringWithoutTagPrefix: String get() = if (string.startsWith("#")) string.substring(1) else string

    public sealed interface CreationFailure : ValidationFailure {
        public data object TagNotWithinRangeFailure :
            CreationFailure
        public data object InvalidFormatFailure :
            CreationFailure
    }

    override fun toString(): String = stringWithTagPrefix
}
