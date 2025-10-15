package com.y9vad9.krawler.auth.domain.value

import com.y9vad9.valdi.domain.ValueObject
import kotlin.time.Instant

/**
 * Represents a recorded friendly battle used to verify ownership challenges.
 *
 * @property brawlerId the ID of the brawler used in the battle
 * @property eventType the event type of the battle; nullable to represent cases where
 *                     the event type is unrecognized or unsupported by the domain
 * @property botsAmount the number of bots involved in the battle
 * @property endTime the timestamp when the battle ended
 */
@ValueObject
public data class LastFriendlyBattle(
    public val brawlerId: OwnershipChallengeBrawlerId,
    public val eventType: OwnershipChallengeEventType?,
    public val botsAmount: OwnershipChallengeBotsAmount,
    public val endTime: Instant,
)
