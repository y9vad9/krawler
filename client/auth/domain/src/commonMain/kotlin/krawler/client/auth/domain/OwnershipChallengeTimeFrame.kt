package krawler.client.auth.domain

import kotlin.time.Duration
import kotlin.time.Instant

public class OwnershipChallengeTimeFrame(
    public val startTime: Instant,
    public val endTime: Instant,
) {
    /**
     * Returns whether the challenge has expired at the given time.
     */
    public fun isExpired(currentTime: Instant): Boolean =
        currentTime > endTime

    /**
     * Returns whether the given timestamp is before the challenge started.
     */
    public fun isBeforeStart(time: Instant): Boolean =
        time < startTime

    /**
     * Returns whether the given timestamp is within the allowed challenge timeframe.
     */
    public fun isWithinTimeFrame(time: Instant): Boolean =
        time in startTime..endTime

    /**
     * Returns the remaining duration until the challenge expires.
     * If already expired, returns Duration.ZERO.
     */
    public fun remainingDuration(currentTime: Instant): Duration =
        if (isExpired(currentTime)) Duration.ZERO else endTime - currentTime
}
