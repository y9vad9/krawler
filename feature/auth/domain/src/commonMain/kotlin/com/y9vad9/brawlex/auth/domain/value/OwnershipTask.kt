package com.y9vad9.brawlex.auth.domain.value

import com.y9vad9.valdi.Factory
import com.y9vad9.valdi.ValidationFailure
import com.y9vad9.valdi.builder.factory
import com.y9vad9.valdi.domain.ValueObject

@ValueObject
public class OwnershipTask private constructor(
    public val brawlerId: OwnershipChallengeBrawlerId,
    public val eventType: OwnershipChallengeEventType,
    public val botsAmount: OwnershipChallengeBotsAmount,
) {
    public companion object {
        public val factory: Factory<Params, OwnershipTask, InvalidBotsAmountForGivenEvent> = factory {
            constraints {
                gives(InvalidBotsAmountForGivenEvent) { params ->
                    when (params.eventType) {
                        OwnershipChallengeEventType.BRAWL_BALL,
                        OwnershipChallengeEventType.GEM_GRAB,
                        OwnershipChallengeEventType.KNOCKOUT -> params.botsAmount.int > 5

                        OwnershipChallengeEventType.SOLO_SHOWDOWN,
                        OwnershipChallengeEventType.DUO_SHOWDOWN -> params.botsAmount.int > 9
                    }
                }
            }

            constructor { params ->
                OwnershipTask(
                    brawlerId = params.brawlerId,
                    eventType = params.eventType,
                    botsAmount = params.botsAmount,
                )
            }
        }
    }

    public data class Params(
        public val brawlerId: OwnershipChallengeBrawlerId,
        public val eventType: OwnershipChallengeEventType,
        public val botsAmount: OwnershipChallengeBotsAmount,
    )

    public data object InvalidBotsAmountForGivenEvent : ValidationFailure
}
