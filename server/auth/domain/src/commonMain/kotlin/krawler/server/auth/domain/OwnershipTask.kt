package krawler.server.auth.domain

/**
 * Represents a task in an ownership challenge, including the brawler, event type,
 * and number of bots involved.
 *
 * Ensures that the number of bots is valid for the given event type.
 *
 * @property brawlerId The brawler participating in the challenge.
 * @property eventType The type of ownership challenge event.
 * @property botsAmount The number of bots involved in the task.
 */
public class OwnershipTask private constructor(
    public val brawlerId: OwnershipChallengeBrawlerId,
    public val eventType: OwnershipChallengeEventType,
    public val botsAmount: OwnershipChallengeBotsAmount,
) {
    public companion object {
        @Suppress("detekt.MagicNumber")
        public fun create(
            brawlerId: OwnershipChallengeBrawlerId,
            eventType: OwnershipChallengeEventType,
            botsAmount: OwnershipChallengeBotsAmount
        ): FactoryResult {
            val isInvalid: Boolean = when (eventType) {
                OwnershipChallengeEventType.BRAWL_BALL,
                OwnershipChallengeEventType.GEM_GRAB,
                OwnershipChallengeEventType.KNOCKOUT -> botsAmount.int > 5

                OwnershipChallengeEventType.SOLO_SHOWDOWN,
                OwnershipChallengeEventType.DUO_SHOWDOWN -> botsAmount.int > 9
            }

            return if (isInvalid) {
                FactoryResult.InvalidBotsAmountForGivenEvent
            } else {
                FactoryResult.Success(
                    OwnershipTask(
                        brawlerId = brawlerId,
                        eventType = eventType,
                        botsAmount = botsAmount
                    )
                )
            }
        }

        public fun createOrNull(
            brawlerId: OwnershipChallengeBrawlerId,
            eventType: OwnershipChallengeEventType,
            botsAmount: OwnershipChallengeBotsAmount
        ): OwnershipTask? =
            (create(brawlerId, eventType, botsAmount) as? FactoryResult.Success)?.value

        public fun createOrThrow(
            brawlerId: OwnershipChallengeBrawlerId,
            eventType: OwnershipChallengeEventType,
            botsAmount: OwnershipChallengeBotsAmount
        ): OwnershipTask {
            val result = create(brawlerId, eventType, botsAmount)
            require(result is FactoryResult.Success) {
                "OwnershipTask creation returned $result instead of success."
            }
            return result.value
        }
    }

    public sealed interface FactoryResult {
        public data object InvalidBotsAmountForGivenEvent : FactoryResult
        public data class Success(public val value: OwnershipTask) : FactoryResult
    }
}
