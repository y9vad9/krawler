package krawler.client.auth.domain.test

import krawler.client.auth.domain.ChallengeId
import krawler.client.auth.domain.ChallengedBrawlStarsPlayerTag
import krawler.client.auth.domain.OwnershipChallenge
import krawler.client.auth.domain.OwnershipChallengeAttempts
import krawler.client.auth.domain.OwnershipChallengeBotsAmount
import krawler.client.auth.domain.OwnershipChallengeBrawlerId
import krawler.client.auth.domain.OwnershipChallengeEventType
import krawler.client.auth.domain.OwnershipChallengeTimeFrame
import krawler.client.auth.domain.OwnershipTask
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant
import kotlin.uuid.Uuid

class OwnershipChallengeTest {
    private val id = ChallengeId(Uuid.random())
    private val playerTag = ChallengedBrawlStarsPlayerTag.createOrThrow("#abcd1234")
    private val brawlerId = OwnershipChallengeBrawlerId(123)
    private val event = OwnershipChallengeEventType.DUO_SHOWDOWN
    private val botsAmount = OwnershipChallengeBotsAmount(3)
    private val startTime = Instant.parse("2025-07-07T00:00:00Z")
    private val endTime = Instant.parse("2025-11-30T00:00:00Z")
    private val timeframe = OwnershipChallengeTimeFrame(
        startTime = startTime,
        endTime = endTime,
    )
    private val attempts = OwnershipChallengeAttempts.createOrThrow(1)
    private val max = OwnershipChallengeAttempts.createOrThrow(9)
    private val task = OwnershipTask.createOrThrow(brawlerId, event, botsAmount)


    @Test
    fun `should not be expired`() {
        // GIVEN
        val input = Instant.parse("2025-08-11T00:00:00Z")

        // WHEN
        val result = OwnershipChallenge(id, playerTag, task, timeframe, attempts, max)

        // THEN
        assertEquals(
            expected = false,
            actual = result.isExpired(input)
        )

    }

    @Test
    fun `should have have till the end`() {
        // GIVEN
        val input = Instant.parse("2025-08-11T00:00:00Z")

        // WHEN
        val result = OwnershipChallenge(id, playerTag, task, timeframe, attempts, max)

        // THEN
        assertEquals(
            expected = endTime - input,
            actual = result.remainingTimeUntilExpiration(input)
        )
    }

    @Test
    fun `attempts are not exceeded`() {
        // WHEN
        val result = OwnershipChallenge(id, playerTag, task, timeframe, attempts, max)

        // THEN
        assertEquals(
            expected = false,
            actual = result.isAttemptsExceeded()
        )
    }
}
