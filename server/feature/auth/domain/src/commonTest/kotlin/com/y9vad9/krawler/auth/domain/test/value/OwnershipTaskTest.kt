package com.y9vad9.krawler.auth.domain.test.value

import com.y9vad9.krawler.auth.domain.value.OwnershipChallengeBotsAmount
import com.y9vad9.krawler.auth.domain.value.OwnershipChallengeEventType
import com.y9vad9.krawler.auth.domain.value.OwnershipTask
import com.y9vad9.krawler.auth.domain.value.OwnershipTask.InvalidBotsAmountForGivenEvent
import com.y9vad9.krawler.auth.domain.value.OwnershipChallengeBrawlerId
import com.y9vad9.valdi.ValidationResult
import com.y9vad9.valdi.createOrThrow
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame

class OwnershipTaskTest {
    private fun validBrawlerId(): OwnershipChallengeBrawlerId =
        // we don't care about wrapped value here
        OwnershipChallengeBrawlerId(Random.nextInt())

    private fun bots(amount: Int): OwnershipChallengeBotsAmount =
        OwnershipChallengeBotsAmount.factory.createOrThrow(amount)

    @Test
    fun `should succeed for 5 bots in GEM_GRAB`() {
        val result = OwnershipTask.factory.create(
            OwnershipTask.Params(
                brawlerId = validBrawlerId(),
                eventType = OwnershipChallengeEventType.GEM_GRAB,
                botsAmount = bots(5),
            )
        )

        assertIs<ValidationResult.Success<OwnershipTask>>(result)
        assertEquals(
            expected = 5,
            actual = result.value.botsAmount.int,
        )
    }

    @Test
    fun `should fail for 6 bots in KNOCKOUT`() {
        val result = OwnershipTask.factory.create(
            OwnershipTask.Params(
                brawlerId = validBrawlerId(),
                eventType = OwnershipChallengeEventType.KNOCKOUT,
                botsAmount = bots(6),
            )
        )

        assertIs<ValidationResult.Failure<*>>(result)
        assertSame(
            expected = InvalidBotsAmountForGivenEvent,
            actual = result.error,
        )
    }

    @Test
    fun `should succeed for 9 bots in SOLO_SHOWDOWN`() {
        val result = OwnershipTask.factory.create(
            OwnershipTask.Params(
                brawlerId = validBrawlerId(),
                eventType = OwnershipChallengeEventType.SOLO_SHOWDOWN,
                botsAmount = bots(9),
            )
        )

        assertIs<ValidationResult.Success<OwnershipTask>>(result)
        assertEquals(
            expected = 9,
            actual = result.value.botsAmount.int,
        )
    }

    @Test
    fun `should fail for 9 bots in KNOCKOUT`() {
        val result = OwnershipTask.factory.create(
            OwnershipTask.Params(
                brawlerId = validBrawlerId(),
                eventType = OwnershipChallengeEventType.KNOCKOUT,
                botsAmount = bots(9),
            )
        )

        assertIs<ValidationResult.Failure<*>>(result)
        assertSame(
            expected = InvalidBotsAmountForGivenEvent,
            actual = result.error,
        )
    }

    @Test
    fun `should succeed for MIN_VALUE bots in BRAWL_BALL`() {
        val result = OwnershipTask.factory.create(
            OwnershipTask.Params(
                brawlerId = validBrawlerId(),
                eventType = OwnershipChallengeEventType.BRAWL_BALL,
                botsAmount = bots(OwnershipChallengeBotsAmount.MIN_VALUE),
            )
        )

        assertIs<ValidationResult.Success<OwnershipTask>>(result)
        assertEquals(
            expected = 0,
            actual = result.value.botsAmount.int,
        )
    }

    @Test
    fun `should fail if validation logic is inverted (negative test)`() {
        // This test is hypothetical — checking that if the logic was incorrectly using < instead of >
        val result = OwnershipTask.factory.create(
            OwnershipTask.Params(
                brawlerId = validBrawlerId(),
                eventType = OwnershipChallengeEventType.GEM_GRAB,
                botsAmount = bots(6), // invalid
            )
        )

        assertIs<ValidationResult.Failure<*>>(result)
        assertSame(
            expected = InvalidBotsAmountForGivenEvent,
            actual = result.error,
        )
    }
}
