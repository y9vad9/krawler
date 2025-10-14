package com.y9vad9.krawler.auth.domain

import com.y9vad9.krawler.auth.domain.event.OwnershipChallengeUpdateEvent
import com.y9vad9.krawler.auth.domain.value.ChallengeId
import com.y9vad9.krawler.auth.domain.value.ChallengedBrawlStarsPlayerTag
import com.y9vad9.krawler.auth.domain.value.ChallengedUserId
import com.y9vad9.krawler.auth.domain.value.LastFriendlyBattle
import com.y9vad9.krawler.auth.domain.value.OwnershipChallengeAttempts
import com.y9vad9.krawler.auth.domain.value.OwnershipChallengeTimeFrame
import com.y9vad9.krawler.auth.domain.value.OwnershipTask
import com.y9vad9.krawler.core.domain.WithDomainUpdateEvent
import com.y9vad9.valdi.domain.AggregateRoot
import kotlin.time.Duration
import kotlin.time.Instant

/**
 * Represents a time-bound ownership challenge that verifies a user controls a specific Brawl Stars player account.
 *
 * The challenge requires the user to complete a specific task in a friendly battle within the defined timeframe
 * and before exceeding the maximum number of allowed attempts.
 *
 * Use [attempt] to evaluate whether a submitted battle satisfies the challenge.
 */
@AggregateRoot
public class OwnershipChallenge(
    public val id: ChallengeId,
    public val userId: ChallengedUserId,
    public val playerTag: ChallengedBrawlStarsPlayerTag,
    public val task: OwnershipTask,
    public val timeframe: OwnershipChallengeTimeFrame,
    public val attempts: OwnershipChallengeAttempts,
    public val maxAttempts: OwnershipChallengeAttempts,
) {
    public fun isExpired(currentTime: Instant): Boolean = timeframe.isExpired(currentTime)
    public fun remainingTimeUntilExpiration(currentTime: Instant): Duration =
        timeframe.remainingDuration(currentTime)
    public fun isAttemptsExceeded(): Boolean = attempts >= maxAttempts

    public fun attempt(
        currentTime: Instant,
        battle: LastFriendlyBattle,
    ): WithDomainUpdateEvent<OwnershipChallengeResult, OwnershipChallengeUpdateEvent?> {
        return when {
            isAttemptsExceeded() -> WithDomainUpdateEvent(
                returning = OwnershipChallengeResult.AttemptsExceeded,
                event = null,
            )
            timeframe.isBeforeStart(battle.endTime) -> WithDomainUpdateEvent(
                OwnershipChallengeResult.BattleBeforeTask,
                OwnershipChallengeUpdateEvent.VerificationFailed(id),
            )
            isExpired(currentTime) -> WithDomainUpdateEvent(OwnershipChallengeResult.TaskExpired, null)
            task.brawlerId != battle.brawlerId -> WithDomainUpdateEvent(
                OwnershipChallengeResult.InvalidBrawler,
                OwnershipChallengeUpdateEvent.VerificationFailed(id),
            )
            task.botsAmount != battle.botsAmount -> WithDomainUpdateEvent(
                OwnershipChallengeResult.InvalidBotsAmount,
                OwnershipChallengeUpdateEvent.VerificationFailed(id),
            )
            task.eventType != battle.eventType -> WithDomainUpdateEvent(
                OwnershipChallengeResult.InvalidEventType,
                OwnershipChallengeUpdateEvent.VerificationFailed(id),
            )
            else -> WithDomainUpdateEvent(
                OwnershipChallengeResult.Success,
                OwnershipChallengeUpdateEvent.VerificationCompleted(id),
            )
        }
    }
}
