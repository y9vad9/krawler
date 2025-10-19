package krawler.auth.domain

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
        battle: OwnershipTaskBattle,
    ): OwnershipChallengeResult {
        return when {
            isAttemptsExceeded() -> OwnershipChallengeResult.AttemptsExceeded
            timeframe.isBeforeStart(battle.endTime) -> OwnershipChallengeResult.BattleBeforeTask
            isExpired(currentTime) -> OwnershipChallengeResult.TaskExpired
            task.brawlerId != battle.brawlerId -> OwnershipChallengeResult.InvalidBrawler
            task.botsAmount != battle.botsAmount -> OwnershipChallengeResult.InvalidBotsAmount
            task.eventType != battle.eventType -> OwnershipChallengeResult.InvalidEventType
            else -> OwnershipChallengeResult.Success
        }
    }
}
