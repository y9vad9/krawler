package krawler.server.player.application.test.battle

import krawler.server.player.application.battle.BattleEventId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BattleEventIdTest {

    private val validId = 15_000_100
    private val minId = BattleEventId.MIN_VALUE
    private val maxId = BattleEventId.MAX_VALUE
    private val invalidIdLow = 14_999_999
    private val invalidIdHigh = 15_100_001

    @Test
    fun `create should return success for valid ID`() {
        // GIVEN a valid event ID
        // WHEN creating a BattleEventId
        val result = BattleEventId.create(validId)

        // THEN the result is successful and contains the correct rawInt
        assertTrue(result.isSuccess)
        assertEquals(validId, result.getOrThrow().rawInt)
    }

    @Test
    fun `create should return failure for invalid ID`() {
        // GIVEN invalid event IDs
        // WHEN creating BattleEventId
        val resultLow = BattleEventId.create(invalidIdLow)
        val resultHigh = BattleEventId.create(invalidIdHigh)

        // THEN the result is failure for both
        assertTrue(resultLow.isFailure)
        assertTrue(resultHigh.isFailure)
    }

    @Test
    fun `createOrThrow should return BattleEventId for valid ID`() {
        // GIVEN a valid event ID
        // WHEN creating with createOrThrow
        val id = BattleEventId.createOrThrow(validId)

        // THEN it returns a BattleEventId with correct value
        assertEquals(validId, id.rawInt)
    }

    @Test
    fun `createOrThrow should throw for invalid ID`() {
        // GIVEN invalid event IDs
        // WHEN creating with createOrThrow
        // THEN it throws IllegalArgumentException
        assertFailsWith<IllegalArgumentException> {
            val _ = BattleEventId.createOrThrow(invalidIdLow)
        }
        assertFailsWith<IllegalArgumentException> {
            val _ = BattleEventId.createOrThrow(invalidIdHigh)
        }
    }

    @Test
    fun `createOrNull should return BattleEventId for valid ID`() {
        // GIVEN a valid event ID
        // WHEN creating with createOrNull
        val id = BattleEventId.createOrNull(validId)

        // THEN it returns a non-null BattleEventId
        assertNotNull(id)
        assertEquals(validId, id.rawInt)
    }

    @Test
    fun `createOrNull should return null for invalid ID`() {
        // GIVEN invalid event IDs
        // WHEN creating with createOrNull
        val idLow = BattleEventId.createOrNull(invalidIdLow)
        val idHigh = BattleEventId.createOrNull(invalidIdHigh)

        // THEN it returns null for both
        assertNull(idLow)
        assertNull(idHigh)
    }

    @Test
    fun `isValid should correctly identify valid and invalid IDs`() {
        // GIVEN a mix of valid and invalid IDs
        // WHEN checking isValid
        // THEN it returns true for valid IDs and false for invalid IDs
        assertTrue(BattleEventId.isValid(validId))
        assertTrue(BattleEventId.isValid(minId))
        assertTrue(BattleEventId.isValid(maxId))
        assertFalse(BattleEventId.isValid(invalidIdLow))
        assertFalse(BattleEventId.isValid(invalidIdHigh))
    }

    @Test
    fun `compareTo should work as expected`() {
        // GIVEN two BattleEventId instances
        val id1 = BattleEventId.createOrThrow(15_000_100)
        val id2 = BattleEventId.createOrThrow(15_000_200)

        // WHEN comparing them
        // THEN the ordering is correct
        assertTrue(id1 < id2)
        assertTrue(id2 > id1)
        assertEquals(0, id1.compareTo(id1))
    }
}
