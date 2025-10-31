package krawler.server.player.application.test.brawler

import krawler.server.player.application.brawler.BrawlerStarPowerId
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BrawlerStarPowerIdTest {

    @Test
    fun `isValid returns true for values within valid range`() {
        // GIVEN values inside the valid Star Power ID range
        val min = BrawlerStarPowerId.MIN_VALUE
        val max = BrawlerStarPowerId.MAX_VALUE
        val mid = (min + max) / 2

        // THEN isValid should return true
        assertTrue(BrawlerStarPowerId.isValid(min))
        assertTrue(BrawlerStarPowerId.isValid(max))
        assertTrue(BrawlerStarPowerId.isValid(mid))
    }

    @Test
    fun `isValid returns false for values outside valid range`() {
        // GIVEN values outside the valid Star Power ID range
        val below = BrawlerStarPowerId.MIN_VALUE - 1
        val above = BrawlerStarPowerId.MAX_VALUE + 1

        // THEN isValid should return false
        assertFalse(BrawlerStarPowerId.isValid(below))
        assertFalse(BrawlerStarPowerId.isValid(above))
    }

    @Test
    fun `create returns success for valid input`() {
        // GIVEN a valid input
        val input = BrawlerStarPowerId.MIN_VALUE

        // THEN create should succeed
        val result = BrawlerStarPowerId.create(input)
        assertTrue(result.isSuccess)
        assertEquals(input, result.getOrThrow().rawInt)
    }

    @Test
    fun `create returns failure for invalid input`() {
        // GIVEN an invalid input
        val input = BrawlerStarPowerId.MAX_VALUE + 10

        // THEN create should fail
        val result = BrawlerStarPowerId.create(input)
        assertTrue(result.isFailure)
    }

    @Test
    fun `createOrThrow returns instance for valid input`() {
        // GIVEN a valid input
        val input = BrawlerStarPowerId.MAX_VALUE

        // THEN createOrThrow should return a valid instance
        val id = BrawlerStarPowerId.createOrThrow(input)
        assertEquals(input, id.rawInt)
    }

    @Test
    fun `createOrThrow throws exception for invalid input`() {
        // GIVEN an invalid input
        val input = BrawlerStarPowerId.MIN_VALUE - 10

        // THEN createOrThrow should throw IllegalArgumentException
        assertFailsWith<IllegalArgumentException> {
            val _ = BrawlerStarPowerId.createOrThrow(input)
        }
    }

    @Test
    fun `createOrNull returns instance for valid input`() {
        // GIVEN a valid input
        val input = BrawlerStarPowerId.MIN_VALUE + 1

        // THEN createOrNull should return a valid instance
        val id = BrawlerStarPowerId.createOrNull(input)
        assertEquals(input, id?.rawInt)
    }

    @Test
    fun `createOrNull returns null for invalid input`() {
        // GIVEN an invalid input
        val input = BrawlerStarPowerId.MAX_VALUE + 100

        // THEN createOrNull should return null
        val id = BrawlerStarPowerId.createOrNull(input)
        assertEquals(null, id)
    }

    @Test
    fun `compareTo reflects numeric ordering correctly`() {
        // GIVEN two-Star Power IDs created through factory
        val lower = BrawlerStarPowerId.createOrThrow(BrawlerStarPowerId.MIN_VALUE)
        val higher = BrawlerStarPowerId.createOrThrow(BrawlerStarPowerId.MAX_VALUE)

        // THEN compareTo should reflect numeric ordering
        assertTrue(lower < higher)
        assertTrue(higher > lower)
        assertEquals(0, lower.compareTo(BrawlerStarPowerId.createOrThrow(BrawlerStarPowerId.MIN_VALUE)))
    }
}
