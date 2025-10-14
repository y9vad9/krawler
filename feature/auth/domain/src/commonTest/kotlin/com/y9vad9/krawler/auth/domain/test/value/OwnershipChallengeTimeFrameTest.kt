package com.y9vad9.krawler.auth.domain.test.value

import com.y9vad9.krawler.auth.domain.value.OwnershipChallengeTimeFrame
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

class OwnershipChallengeTimeFrameTest {
    private val startTime = Instant.parse("2025-07-23T12:00:00Z")
    private val endTime = Instant.parse("2025-07-23T13:00:00Z")
    private val timeframe = OwnershipChallengeTimeFrame(
        startTime = startTime,
        endTime = endTime,
    )

    @Test
    fun `isExpired returns false when current time is before end time`() {
        val currentTime = endTime - 1.seconds

        assertEquals(
            expected = false,
            actual = timeframe.isExpired(currentTime),
        )
    }

    @Test
    fun `isExpired returns false when current time is exactly end time`() {
        val currentTime = endTime

        assertEquals(
            expected = false,
            actual = timeframe.isExpired(currentTime),
        )
    }

    @Test
    fun `isExpired returns true when current time is after end time`() {
        val currentTime = endTime + 1.seconds

        assertEquals(
            expected = true,
            actual = timeframe.isExpired(currentTime),
        )
    }

    @Test
    fun `isBeforeStart returns true when time is before start time`() {
        val time = startTime - 1.seconds

        assertEquals(
            expected = true,
            actual = timeframe.isBeforeStart(time),
        )
    }

    @Test
    fun `isBeforeStart returns false when time is exactly start time`() {
        val time = startTime

        assertEquals(
            expected = false,
            actual = timeframe.isBeforeStart(time),
        )
    }

    @Test
    fun `isBeforeStart returns false when time is after start time`() {
        val time = startTime + 1.seconds

        assertEquals(
            expected = false,
            actual = timeframe.isBeforeStart(time),
        )
    }

    @Test
    fun `isWithinTimeFrame returns false when time is before start`() {
        val time = startTime - 1.seconds

        assertEquals(
            expected = false,
            actual = timeframe.isWithinTimeFrame(time),
        )
    }

    @Test
    fun `isWithinTimeFrame returns true when time is exactly start`() {
        val time = startTime

        assertEquals(
            expected = true,
            actual = timeframe.isWithinTimeFrame(time),
        )
    }

    @Test
    fun `isWithinTimeFrame returns true when time is between start and end`() {
        val time = startTime + 30.minutes

        assertEquals(
            expected = true,
            actual = timeframe.isWithinTimeFrame(time),
        )
    }

    @Test
    fun `isWithinTimeFrame returns true when time is exactly end`() {
        val time = endTime

        assertEquals(
            expected = true,
            actual = timeframe.isWithinTimeFrame(time),
        )
    }

    @Test
    fun `isWithinTimeFrame returns false when time is after end`() {
        val time = endTime + 1.seconds

        assertEquals(
            expected = false,
            actual = timeframe.isWithinTimeFrame(time),
        )
    }

    @Test
    fun `remainingDuration returns positive duration when not expired`() {
        val currentTime = startTime + 15.minutes

        assertEquals(
            expected = endTime - currentTime,
            actual = timeframe.remainingDuration(currentTime),
        )
    }

    @Test
    fun `remainingDuration returns zero when current time is exactly end`() {
        val currentTime = endTime

        assertEquals(
            expected = Duration.ZERO,
            actual = timeframe.remainingDuration(currentTime),
        )
    }

    @Test
    fun `remainingDuration returns zero when current time is after end`() {
        val currentTime = endTime + 10.seconds

        assertEquals(
            expected = Duration.ZERO,
            actual = timeframe.remainingDuration(currentTime),
        )
    }
}
