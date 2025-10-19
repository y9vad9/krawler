package krawler.auth.domain.test

import krawler.auth.domain.OwnershipChallenge
import krawler.auth.domain.OwnershipChallengeResult
import krawler.auth.domain.ChallengeId
import krawler.auth.domain.ChallengedBrawlStarsPlayerTag
import krawler.auth.domain.ChallengedUserId
import krawler.auth.domain.OwnershipTaskBattle
import krawler.auth.domain.OwnershipChallengeAttempts
import krawler.auth.domain.OwnershipChallengeBotsAmount
import krawler.auth.domain.OwnershipChallengeBrawlerId
import krawler.auth.domain.OwnershipChallengeEventType
import krawler.auth.domain.OwnershipChallengeTimeFrame
import krawler.auth.domain.OwnershipTask
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlin.uuid.Uuid

class OwnershipChallengeTest {

    // region — Core business rule tests

    @Test
    fun `should return AttemptsExceeded if current attempts are equal to max`() {
        // GIVEN
        val challenge = challenge(currentAttempts = 3, maxAttempts = 3)

        // WHEN
        val result = challenge.attempt(
            currentTime = now,
            battle = matchingBattle(),
        )

        // THEN
        assertEquals(OwnershipChallengeResult.AttemptsExceeded, result)
    }

    @Test
    fun `should return BattleBeforeTask if battle ends before timeframe`() {
        // GIVEN
        val tf = timeframe(startOffset = 1, endOffset = 10)
        val battle = matchingBattle(endTime = now)
        val challenge = challenge(timeframe = tf)

        // WHEN
        val result = challenge.attempt(
            currentTime = now,
            battle = battle,
        )

        // THEN
        assertEquals(OwnershipChallengeResult.BattleBeforeTask, result)
    }

    @Test
    fun `should return TaskExpired if current time is after end time`() {
        // GIVEN
        val tf = timeframe(startOffset = -10, endOffset = -1)
        val challenge = challenge(timeframe = tf)

        // WHEN
        val result = challenge.attempt(
            currentTime = now,
            battle = matchingBattle(),
        )

        // THEN
        assertEquals(OwnershipChallengeResult.TaskExpired, result)
    }

    @Test
    fun `should return InvalidBrawler if brawlerId does not match`() {
        // GIVEN
        val invalidBattle = matchingBattle().copy(
            brawlerId = OwnershipChallengeBrawlerId(0),
        )
        val challenge = challenge()

        // WHEN
        val result = challenge.attempt(
            currentTime = now,
            battle = invalidBattle,
        )

        // THEN
        assertEquals(OwnershipChallengeResult.InvalidBrawler, result)
    }

    @Test
    fun `should return InvalidEventType if event type mismatches`() {
        // GIVEN
        val invalidBattle = matchingBattle().copy(
            eventType = OwnershipChallengeEventType.BRAWL_BALL,
        )
        val challenge = challenge()

        // WHEN
        val result = challenge.attempt(
            currentTime = now,
            battle = invalidBattle,
        )

        // THEN
        assertEquals(OwnershipChallengeResult.InvalidEventType, result)
    }

    @Test
    fun `should return InvalidBotsAmount if bot count mismatches`() {
        // GIVEN
        val invalidBattle = matchingBattle().copy(
            botsAmount = OwnershipChallengeBotsAmount.createOrThrow(2),
        )
        val challenge = challenge()

        // WHEN
        val result = challenge.attempt(
            currentTime = now,
            battle = invalidBattle,
        )

        // THEN
        assertEquals(OwnershipChallengeResult.InvalidBotsAmount, result)
    }

    @Test
    fun `should return Success and VerificationCompleted for valid battle`() {
        // GIVEN
        val challenge = challenge()
        val battle = matchingBattle()

        // WHEN
        val result = challenge.attempt(
            currentTime = now,
            battle = battle,
        )

        // THEN
        assertEquals(OwnershipChallengeResult.Success, result)
    }

    // endregion

    // region — Consistency checks for factory-style value objects

    @Test
    fun `OwnershipChallengeAttempts createOrNull and createOrThrow are consistent`() {
        // GIVEN
        val valid = 2
        val invalid = -1

        // WHEN / THEN
        assertNotNull(OwnershipChallengeAttempts.createOrNull(valid))
        assertFailsWith<IllegalArgumentException> {
            @Suppress("RETURN_VALUE_NOT_USED")
            OwnershipChallengeAttempts.createOrThrow(invalid)
        }
        assertNull(OwnershipChallengeAttempts.createOrNull(invalid))
    }

    @Test
    fun `OwnershipChallengeBotsAmount createOrNull and createOrThrow are consistent`() {
        // GIVEN
        val valid = 3
        val invalid = -1

        // WHEN / THEN
        assertNotNull(OwnershipChallengeBotsAmount.createOrNull(valid))
        assertFailsWith<IllegalArgumentException> {
            @Suppress("RETURN_VALUE_NOT_USED")
            OwnershipChallengeBotsAmount.createOrThrow(invalid)
        }
        assertNull(OwnershipChallengeBotsAmount.createOrNull(invalid))
    }

    // endregion

    // region — Helpers

    private val now = Clock.System.now()
    private val defaultChallengeId = Uuid.random()
    private val defaultUserId = Uuid.random()

    private fun id() = ChallengeId(defaultChallengeId)
    private fun userId() = ChallengedUserId(defaultUserId)
    private fun playerTag() = ChallengedBrawlStarsPlayerTag.createOrThrow("#ABCDFE")
    private fun attempts(v: Int) = OwnershipChallengeAttempts.createOrThrow(v)
    private fun bots(v: Int) = OwnershipChallengeBotsAmount.createOrThrow(v)

    private fun task(): OwnershipTask = OwnershipTask.createOrThrow(
        brawlerId = OwnershipChallengeBrawlerId(16_000_000),
        eventType = OwnershipChallengeEventType.GEM_GRAB,
        botsAmount = bots(3),
    )

    private fun timeframe(startOffset: Long = -1, endOffset: Long = 1) = OwnershipChallengeTimeFrame(
        startTime = now + startOffset.minutes,
        endTime = now + endOffset.minutes,
    )

    private fun challenge(
        currentAttempts: Int = 0,
        maxAttempts: Int = 3,
        timeframe: OwnershipChallengeTimeFrame = timeframe(),
        task: OwnershipTask = task(),
    ): OwnershipChallenge = OwnershipChallenge(
        id = id(),
        userId = userId(),
        playerTag = playerTag(),
        task = task,
        timeframe = timeframe,
        attempts = attempts(currentAttempts),
        maxAttempts = attempts(maxAttempts),
    )

    private fun matchingBattle(endTime: Instant = now): OwnershipTaskBattle = OwnershipTaskBattle(
        brawlerId = OwnershipChallengeBrawlerId(16_000_000),
        eventType = OwnershipChallengeEventType.GEM_GRAB,
        botsAmount = bots(3),
        endTime = endTime,
    )

    // endregion
}
