package krawler.client.auth.domain

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
    }
