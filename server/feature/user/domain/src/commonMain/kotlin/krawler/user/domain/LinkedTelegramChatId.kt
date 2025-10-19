package krawler.user.domain

import krawler.core.domain.ValueObject

/**
 * Represents a linked Telegram chat ID.
 *
 * Ensures the ID is non-negative ([MIN_VALUE]) and provides factory methods for safe creation.
 *
 * @property long The Telegram chat ID.
 */
@JvmInline
@ValueObject
public value class LinkedTelegramChatId private constructor(public val long: Long) {

    public companion object {
        public const val MIN_VALUE: Long = 0L

        public fun create(long: Long): FactoryResult =
            if (long < MIN_VALUE) {
                FactoryResult.ValueLessThanRequiredMinimum
            } else {
                FactoryResult.Success(LinkedTelegramChatId(long))
            }

        public fun createOrNull(long: Long): LinkedTelegramChatId? =
            (create(long) as? FactoryResult.Success)?.value

        public fun createOrThrow(long: Long): LinkedTelegramChatId {
            val result = create(long)
            require(result is FactoryResult.Success) {
                "LinkedTelegramChatId creation returned $result instead of success."
            }
            return result.value
        }
    }

    public sealed interface FactoryResult {
        public data object ValueLessThanRequiredMinimum : FactoryResult
        public data class Success(public val value: LinkedTelegramChatId) : FactoryResult
    }
}

