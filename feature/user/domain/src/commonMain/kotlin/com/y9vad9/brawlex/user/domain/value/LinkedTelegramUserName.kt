package com.y9vad9.brawlex.user.domain.value

import com.y9vad9.valdi.Factory
import com.y9vad9.valdi.ValidationFailure
import com.y9vad9.valdi.builder.factory
import com.y9vad9.valdi.domain.ValueObject
import kotlin.jvm.JvmInline

/**
 * Represents a displayable name chosen by a user within the system.
 *
 * This name is not tied to any external account (e.g., Brawl Stars player tag or Telegram handle),
 * but is rather an identity the user sets for themselves when they first interact with the bot.
 * Users can change this name later if desired.
 *
 * The [LinkedTelegramUserName] is a value object with enforced constraints:
 * - Minimum length: [MIN_LENGTH] character(s)
 * - Maximum length: [MAX_LENGTH] characters
 *
 * Use [factory] to construct validated instances and handle potential [NameNotWithinRangeFailure] cases.
 */
@ValueObject
@JvmInline
public value class LinkedTelegramUserName private constructor(public val string: String) {

    public companion object Companion {
        public const val MIN_LENGTH: Int = 1
        public const val MAX_LENGTH: Int = 100

        public val LENGTH_RANGE: IntRange = MIN_LENGTH..MAX_LENGTH

        public val factory: Factory<String, LinkedTelegramUserName, NameNotWithinRangeFailure> = factory {
            constraints {
                gives(NameNotWithinRangeFailure) { input -> input.length !in LENGTH_RANGE }
            }

            constructor(::LinkedTelegramUserName)
        }
    }

    public data object NameNotWithinRangeFailure : ValidationFailure
}
