package krawler.server.player.application.test.brawler

import krawler.server.player.application.brawler.BrawlerGadgetId
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BrawlerGadgetIdTest {

    @Test
    fun `isValid returns true for values within range`() {
        // GIVEN values inside the valid gadget ID range
        val validValue = BrawlerGadgetId.MIN_VALUE
        val validValue2 = BrawlerGadgetId.MAX_VALUE

        // THEN isValid should return true
        assertTrue(BrawlerGadgetId.isValid(validValue))
        assertTrue(BrawlerGadgetId.isValid(validValue2))
    }

    @Test
    fun `isValid returns false for values outside range`() {
        // GIVEN values outside the valid gadget ID range
        val below = BrawlerGadgetId.MIN_VALUE - 1
        val above = BrawlerGadgetId.MAX_VALUE + 1

        // THEN isValid should return false
        assertFalse(BrawlerGadgetId.isValid(below))
        assertFalse(BrawlerGadgetId.isValid(above))
    }

    @Test
    fun `create returns success for valid input`() {
        // GIVEN a valid gadget ID
        val input = BrawlerGadgetId.MIN_VALUE

        // THEN create should succeed
        val result = BrawlerGadgetId.create(input)
        assertTrue(result.isSuccess)
        assertEquals(input, result.getOrThrow().rawInt)
    }

    @Test
    fun `create returns failure for invalid input`() {
        // GIVEN an invalid gadget ID
        val input = BrawlerGadgetId.MIN_VALUE - 1

        // THEN create should fail
        val result = BrawlerGadgetId.create(input)
        assertTrue(result.isFailure)
    }

    @Test
    fun `createOrThrow returns instance for valid input`() {
        // GIVEN a valid gadget ID
        val input = BrawlerGadgetId.MAX_VALUE

        // THEN createOrThrow should return an instance
        val id = BrawlerGadgetId.createOrThrow(input)
        assertEquals(input, id.rawInt)
    }

    @Test
    fun `createOrThrow throws exception for invalid input`() {
        // GIVEN an invalid gadget ID
        val input = BrawlerGadgetId.MAX_VALUE + 1

        // THEN createOrThrow should throw IllegalArgumentException
        assertFailsWith<IllegalArgumentException> {
            val _ = BrawlerGadgetId.createOrThrow(input)
        }
    }

    @Test
    fun `createOrNull returns instance for valid input`() {
        // GIVEN a valid gadget ID
        val input = BrawlerGadgetId.MIN_VALUE

        // THEN createOrNull should return an instance
        val id = BrawlerGadgetId.createOrNull(input)
        assertEquals(input, id?.rawInt)
    }

    @Test
    fun `createOrNull returns null for invalid input`() {
        // GIVEN an invalid gadget ID
        val input = BrawlerGadgetId.MIN_VALUE - 1

        // THEN createOrNull should return null
        val id = BrawlerGadgetId.createOrNull(input)
        assertEquals(null, id)
    }

    @Test
    fun `compareTo returns correct ordering`() {
        // GIVEN two gadget IDs
        val lower = BrawlerGadgetId.createOrThrow(BrawlerGadgetId.MIN_VALUE)
        val higher = BrawlerGadgetId.createOrThrow(BrawlerGadgetId.MAX_VALUE)

        // THEN compareTo should reflect numeric ordering
        assertTrue(lower < higher)
        assertTrue(higher > lower)
        assertEquals(0, lower.compareTo(BrawlerGadgetId.createOrThrow(BrawlerGadgetId.MIN_VALUE)))
    }
}
