package krawler.server.player.application.test.brawler

import krawler.server.player.application.brawler.BrawlerId
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BrawlerIdTest {

    @Test
    fun `isValid returns true for IDs within valid range`() {
        // GIVEN values inside the valid brawler ID range
        val min = BrawlerId.MIN_VALUE
        val max = BrawlerId.MAX_VALUE

        // THEN isValid should return true
        assertTrue(BrawlerId.isValid(min))
        assertTrue(BrawlerId.isValid(max))
    }

    @Test
    fun `isValid returns false for IDs outside valid range`() {
        // GIVEN values outside the valid brawler ID range
        val below = BrawlerId.MIN_VALUE - 1
        val above = BrawlerId.MAX_VALUE + 1

        // THEN isValid should return false
        assertFalse(BrawlerId.isValid(below))
        assertFalse(BrawlerId.isValid(above))
    }

    @Test
    fun `create returns success for valid input`() {
        // GIVEN a valid ID
        val input = BrawlerId.MIN_VALUE

        // THEN create should succeed
        val result = BrawlerId.create(input)
        assertTrue(result.isSuccess)
        assertEquals(input, result.getOrThrow().rawInt)
    }

    @Test
    fun `create returns failure for invalid input`() {
        // GIVEN an invalid ID
        val input = BrawlerId.MIN_VALUE - 1

        // THEN create should fail
        val result = BrawlerId.create(input)
        assertTrue(result.isFailure)
    }

    @Test
    fun `createOrThrow returns instance for valid input`() {
        // GIVEN a valid ID
        val input = BrawlerId.MAX_VALUE

        // THEN createOrThrow should return an instance
        val id = BrawlerId.createOrThrow(input)
        assertEquals(input, id.rawInt)
    }

    @Test
    fun `createOrThrow throws exception for invalid input`() {
        // GIVEN an invalid ID
        val input = BrawlerId.MAX_VALUE + 1

        // THEN createOrThrow should throw IllegalArgumentException
        assertFailsWith<IllegalArgumentException> {
            val _ = BrawlerId.createOrThrow(input)
        }
    }

    @Test
    fun `createOrNull returns instance for valid input`() {
        // GIVEN a valid ID
        val input = BrawlerId.MIN_VALUE

        // THEN createOrNull should return an instance
        val id = BrawlerId.createOrNull(input)
        assertEquals(input, id?.rawInt)
    }

    @Test
    fun `createOrNull returns null for invalid input`() {
        // GIVEN an invalid ID
        val input = BrawlerId.MIN_VALUE - 1

        // THEN createOrNull should return null
        val id = BrawlerId.createOrNull(input)
        assertEquals(null, id)
    }

    @Test
    fun `compareTo reflects numeric ordering correctly`() {
        // GIVEN two BrawlerIds created through factory
        val lower = BrawlerId.createOrThrow(BrawlerId.MIN_VALUE)
        val higher = BrawlerId.createOrThrow(BrawlerId.MAX_VALUE)

        // THEN compareTo should reflect numeric ordering
        assertTrue(lower < higher)
        assertTrue(higher > lower)
        assertEquals(0, lower.compareTo(BrawlerId.createOrThrow(BrawlerId.MIN_VALUE)))
    }

    @Test
    fun `predefined constants SHELLY and COLT have correct rawInt`() {
        // THEN constants should match expected IDs
        assertEquals(16_000_000, BrawlerId.SHELLY.rawInt)
        assertEquals(16_000_001, BrawlerId.COLT.rawInt)
    }
}
