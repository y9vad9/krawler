package krawler.user.domain

import krawler.core.domain.ValueObject
import kotlin.jvm.JvmInline

/**
 * Represents a displayable name chosen by a user within the system.
 *
 * This name is independent of any external account (e.g., Brawl Stars player tag or Telegram handle),
 * and can be changed by the user later if desired.
 *
 * Validation rules:
 * - Minimum length: [MIN_LENGTH] character(s)
 * - Maximum length: [MAX_LENGTH] characters
 *
 * Use the factory methods ([create], [createOrNull], [createOrThrow]) to safely construct instances.
 *
 * @property string The user's display name.
 */
@ValueObject
@JvmInline
public value class LinkedTelegramUserName private constructor(public val string: String) {

    public companion object {
        public const val MIN_LENGTH: Int = 1
        public const val MAX_LENGTH: Int = 100
        private val LENGTH_RANGE: IntRange = MIN_LENGTH..MAX_LENGTH

        public fun create(name: String): FactoryResult =
            if (name.length !in LENGTH_RANGE) {
                FactoryResult.NameNotWithinRange
            } else {
                FactoryResult.Success(LinkedTelegramUserName(name))
            }

        public fun createOrNull(name: String): LinkedTelegramUserName? =
            (create(name) as? FactoryResult.Success)?.value

        public fun createOrThrow(name: String): LinkedTelegramUserName {
            val result = create(name)
            require(result is FactoryResult.Success) {
                "LinkedTelegramUserName creation returned $result instead of success."
            }
            return result.value
        }
    }

    public sealed interface FactoryResult {
        public data object NameNotWithinRange : FactoryResult
        public data class Success(public val value: LinkedTelegramUserName) : FactoryResult
    }
}
