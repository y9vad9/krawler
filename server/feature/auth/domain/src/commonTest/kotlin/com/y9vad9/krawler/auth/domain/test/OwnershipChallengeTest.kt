package com.y9vad9.krawler.auth.domain.test

import com.y9vad9.krawler.auth.domain.OwnershipChallenge
import com.y9vad9.krawler.auth.domain.OwnershipChallengeResult
import com.y9vad9.krawler.auth.domain.event.OwnershipChallengeUpdateEvent
import com.y9vad9.krawler.auth.domain.value.ChallengeId
import com.y9vad9.krawler.auth.domain.value.ChallengedBrawlStarsPlayerTag
import com.y9vad9.krawler.auth.domain.value.ChallengedUserId
import com.y9vad9.krawler.auth.domain.value.LastFriendlyBattle
import com.y9vad9.krawler.auth.domain.value.OwnershipChallengeAttempts
import com.y9vad9.krawler.auth.domain.value.OwnershipChallengeBotsAmount
import com.y9vad9.krawler.auth.domain.value.OwnershipChallengeBrawlerId
import com.y9vad9.krawler.auth.domain.value.OwnershipChallengeEventType
import com.y9vad9.krawler.auth.domain.value.OwnershipChallengeTimeFrame
import com.y9vad9.krawler.auth.domain.value.OwnershipTask
import com.y9vad9.valdi.createOrThrow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlin.uuid.Uuid

class OwnershipChallengeTest {
    @Test
    fun `should return AttemptsExceeded if current attempts are equal to max`() {
        val result = challenge(currentAttempts = 3).attempt(
            currentTime = now,
            battle = matchingBattle(),
        )

        assertEquals(
            expected = OwnershipChallengeResult.AttemptsExceeded,
            actual = result.returning,
        )
        assertEquals(
            expected = null,
            actual = result.event,
        )
    }

    @Test
    fun `should return BattleBeforeTask if battle ends before timeframe`() {
        val tf = timeframe(startOffset = 1, endOffset = 10)
        val battle = matchingBattle(endTime = now)

        val result = challenge(timeframe = tf).attempt(
            currentTime = now,
            battle = battle,
        )

        assertEquals(
            expected = OwnershipChallengeResult.BattleBeforeTask,
            actual = result.returning,
        )
        assertIs<OwnershipChallengeUpdateEvent.VerificationFailed>(result.event)
        assertEquals(
            expected = id(),
            actual = (result.event as OwnershipChallengeUpdateEvent.VerificationFailed).challengeId,
        )
    }

    @Test
    fun `should return TaskExpired if current time is after end time`() {
        val tf = timeframe(startOffset = -10, endOffset = -1)

        val result = challenge(timeframe = tf).attempt(
            currentTime = now,
            battle = matchingBattle(),
        )

        assertEquals(
            expected = OwnershipChallengeResult.TaskExpired,
            actual = result.returning,
        )
        assertEquals(
            expected = null,
            actual = result.event,
        )
    }

    @Test
    fun `should return InvalidBrawler if brawlerId does not match`() {
        val invalidBattle = matchingBattle().let { battle ->
            LastFriendlyBattle(
                brawlerId = OwnershipChallengeBrawlerId(0),
                eventType = battle.eventType,
                botsAmount = battle.botsAmount,
                endTime = battle.endTime,
            )
        }

        val result = challenge().attempt(
            currentTime = now,
            battle = invalidBattle,
        )

        assertEquals(
            expected = OwnershipChallengeResult.InvalidBrawler,
            actual = result.returning,
        )
        assertIs<OwnershipChallengeUpdateEvent.VerificationFailed>(result.event)
    }

    @Test
    fun `should return InvalidEventType if event type mismatches`() {
        val invalidBattle = matchingBattle().let { battle ->
            LastFriendlyBattle(
                brawlerId = battle.brawlerId,
                eventType = OwnershipChallengeEventType.BRAWL_BALL,
                botsAmount = battle.botsAmount,
                endTime = battle.endTime,
            )
        }

        val result = challenge().attempt(
            currentTime = now,
            battle = invalidBattle,
        )

        assertEquals(
            expected = OwnershipChallengeResult.InvalidEventType,
            actual = result.returning,
        )
        assertIs<OwnershipChallengeUpdateEvent.VerificationFailed>(result.event)
    }

    @Test
    fun `should return InvalidBotsAmount if bot count mismatches`() {
        val invalidBattle = matchingBattle().let { battle ->
            LastFriendlyBattle(
                brawlerId = battle.brawlerId,
                eventType = battle.eventType,
                botsAmount = OwnershipChallengeBotsAmount.factory.createOrThrow(2),
                endTime = battle.endTime,
            )
        }

        val result = challenge().attempt(
            currentTime = now,
            battle = invalidBattle,
        )

        assertEquals(
            expected = OwnershipChallengeResult.InvalidBotsAmount,
            actual = result.returning,
        )
        assertIs<OwnershipChallengeUpdateEvent.VerificationFailed>(result.event)
    }

    @Test
    fun `should return Success and VerificationCompleted for valid battle`() {
        val challenge = challenge()
        val battle = matchingBattle()

        val result = challenge.attempt(
            currentTime = now,
            battle = battle,
        )

        assertEquals(
            expected = OwnershipChallengeResult.Success,
            actual = result.returning,
        )
        assertIs<OwnershipChallengeUpdateEvent.VerificationCompleted>(result.event)
        assertEquals(
            expected = challenge.id,
            actual = (result.event as OwnershipChallengeUpdateEvent.VerificationCompleted).challengeId,
        )
    }

    private val now = Clock.System.now()

    private val defaultChallengeId = Uuid.random()
    private val defaultUserId = Uuid.random()

    private fun id() = ChallengeId(defaultChallengeId)
    private fun userId() = ChallengedUserId(defaultUserId)
    private fun playerTag() = ChallengedBrawlStarsPlayerTag.factory.createOrThrow("#ABCDFE")
    private fun attempts(v: Int) = OwnershipChallengeAttempts.factory.createOrThrow(v)
    private fun bots(v: Int) = OwnershipChallengeBotsAmount.factory.createOrThrow(v)
    private fun task(): OwnershipTask = OwnershipTask.factory.createOrThrow(
        OwnershipTask.Params(
            brawlerId = OwnershipChallengeBrawlerId(16_000_000),
            eventType = OwnershipChallengeEventType.GEM_GRAB,
            botsAmount = bots(3),
        )
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

    private fun matchingBattle(endTime: Instant = now): LastFriendlyBattle = LastFriendlyBattle(
        brawlerId = OwnershipChallengeBrawlerId(16_000_000),
        eventType = OwnershipChallengeEventType.GEM_GRAB,
        botsAmount = bots(3),
        endTime = endTime,
    )
}
