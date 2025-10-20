package krawler.server.auth.domain.test

import krawler.server.auth.domain.OwnershipChallengeBotsAmount
import krawler.server.auth.domain.OwnershipChallengeBrawlerId
import krawler.server.auth.domain.OwnershipChallengeEventType
import krawler.server.auth.domain.OwnershipTask
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame

class OwnershipTaskTest {

    // region — Helper functions

    private fun validBrawlerId(): OwnershipChallengeBrawlerId =
        OwnershipChallengeBrawlerId(Random.nextInt())

    private fun bots(amount: Int): OwnershipChallengeBotsAmount =
        OwnershipChallengeBotsAmount.createOrThrow(amount)

    // endregion

    // region — Core factory tests

    @Test
    fun `should succeed for 5 bots in GEM_GRAB`() {
        // GIVEN
        val brawlerId = validBrawlerId()
        val botsAmount = bots(5)

        // WHEN
        val result = OwnershipTask.create(
            brawlerId = brawlerId,
            eventType = OwnershipChallengeEventType.GEM_GRAB,
            botsAmount = botsAmount,
        )

        // THEN
        assertIs<OwnershipTask.FactoryResult.Success>(result)
        assertEquals(expected = 5, actual = result.value.botsAmount.int)
    }

    @Test
    fun `should fail for 6 bots in KNOCKOUT`() {
        // GIVEN
        val brawlerId = validBrawlerId()
        val botsAmount = bots(6)

        // WHEN
        val result = OwnershipTask.create(
            brawlerId = brawlerId,
            eventType = OwnershipChallengeEventType.KNOCKOUT,
            botsAmount = botsAmount,
        )

        // THEN
        assertIs<OwnershipTask.FactoryResult.InvalidBotsAmountForGivenEvent>(result)
    }

    @Test
    fun `should succeed for 9 bots in SOLO_SHOWDOWN`() {
        // GIVEN
        val brawlerId = validBrawlerId()
        val botsAmount = bots(9)

        // WHEN
        val result = OwnershipTask.create(
            brawlerId = brawlerId,
            eventType = OwnershipChallengeEventType.SOLO_SHOWDOWN,
            botsAmount = botsAmount,
        )

        // THEN
        assertIs<OwnershipTask.FactoryResult.Success>(result)
        assertEquals(expected = 9, actual = result.value.botsAmount.int)
    }

    @Test
    fun `should fail for 9 bots in KNOCKOUT`() {
        // GIVEN
        val brawlerId = validBrawlerId()
        val botsAmount = bots(9)

        // WHEN
        val result = OwnershipTask.create(
            brawlerId = brawlerId,
            eventType = OwnershipChallengeEventType.KNOCKOUT,
            botsAmount = botsAmount,
        )

        // THEN
        assertIs<OwnershipTask.FactoryResult.InvalidBotsAmountForGivenEvent>(result)
    }

    @Test
    fun `should succeed for MIN_VALUE bots in BRAWL_BALL`() {
        // GIVEN
        val brawlerId = validBrawlerId()
        val botsAmount = bots(OwnershipChallengeBotsAmount.MIN_VALUE)

        // WHEN
        val result = OwnershipTask.create(
            brawlerId = brawlerId,
            eventType = OwnershipChallengeEventType.BRAWL_BALL,
            botsAmount = botsAmount,
        )

        // THEN
        assertIs<OwnershipTask.FactoryResult.Success>(result)
        assertEquals(expected = 0, actual = result.value.botsAmount.int)
    }

    @Test
    fun `should fail for invalid logic (negative test)`() {
        // GIVEN
        val brawlerId = validBrawlerId()
        val botsAmount = bots(6)

        // WHEN
        val result = OwnershipTask.create(
            brawlerId = brawlerId,
            eventType = OwnershipChallengeEventType.GEM_GRAB, // invalid for 6 bots
            botsAmount = botsAmount,
        )

        // THEN
        assertIs<OwnershipTask.FactoryResult.InvalidBotsAmountForGivenEvent>(result)
        assertSame(
            expected = OwnershipTask.FactoryResult.InvalidBotsAmountForGivenEvent,
            actual = result,
        )
    }

    // endregion

    // region — Consistency checks for factory-style value objects

    @Test
    fun `BotsAmount factory createOrNull and createOrThrow consistency`() {
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
}
