package krawler.server.player.application.test.brawler

import krawler.server.player.application.brawler.BrawlerPowerLevel
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BrawlerPowerLevelTest {

    @Test
    fun `isValid returns true for values within valid range`() {
        // GIVEN values inside the valid power level range
        val min = BrawlerPowerLevel.MIN_VALUE
        val max = BrawlerPowerLevel.MAX_VALUE

        // THEN isValid should return true
        assertTrue(BrawlerPowerLevel.isValid(min))
        assertTrue(BrawlerPowerLevel.isValid(max))
        assertTrue(BrawlerPowerLevel.isValid(5))
    }

    @Test
    fun `isValid returns false for values outside valid range`() {
        // GIVEN values outside the valid power level range
        val below = BrawlerPowerLevel.MIN_VALUE - 1
        val above = BrawlerPowerLevel.MAX_VALUE + 1

        // THEN isValid should return false
        assertFalse(BrawlerPowerLevel.isValid(below))
        assertFalse(BrawlerPowerLevel.isValid(above))
    }

    @Test
    fun `create returns success for valid input`() {
        // GIVEN a valid input
        val input = 7

        // THEN create should succeed
        val result = BrawlerPowerLevel.create(input)
        assertTrue(result.isSuccess)
        assertEquals(input, result.getOrThrow().int)
    }

    @Test
    fun `create returns failure for invalid input`() {
        // GIVEN an invalid input
        val input = BrawlerPowerLevel.MAX_VALUE + 1

        // THEN create should fail
        val result = BrawlerPowerLevel.create(input)
        assertTrue(result.isFailure)
    }

    @Test
    fun `createOrThrow returns instance for valid input`() {
        // GIVEN a valid input
        val input = BrawlerPowerLevel.MIN_VALUE

        // THEN createOrThrow should return a valid instance
        val level = BrawlerPowerLevel.createOrThrow(input)
        assertEquals(input, level.int)
    }

    @Test
    fun `createOrThrow throws exception for invalid input`() {
        // GIVEN an invalid input
        val input = BrawlerPowerLevel.MIN_VALUE - 1

        // THEN createOrThrow should throw IllegalArgumentException
        assertFailsWith<IllegalArgumentException> {
            val _ = BrawlerPowerLevel.createOrThrow(input)
        }
    }

    @Test
    fun `createOrNull returns instance for valid input`() {
        // GIVEN a valid input
        val input = 3

        // THEN createOrNull should return a valid instance
        val level = BrawlerPowerLevel.createOrNull(input)
        assertEquals(input, level?.int)
    }

    @Test
    fun `createOrNull returns null for invalid input`() {
        // GIVEN an invalid input
        val input = 12

        // THEN createOrNull should return null
        val level = BrawlerPowerLevel.createOrNull(input)
        assertEquals(null, level)
    }

    @Test
    fun `compareTo reflects numeric ordering correctly`() {
        // GIVEN two power levels created through factory
        val lower = BrawlerPowerLevel.createOrThrow(2)
        val higher = BrawlerPowerLevel.createOrThrow(10)

        // THEN compareTo should reflect numeric ordering
        assertTrue(lower < higher)
        assertTrue(higher > lower)
        assertEquals(0, lower.compareTo(BrawlerPowerLevel.createOrThrow(2)))
    }

    @Test
    fun `predefined constants MIN and MAX have correct int values`() {
        // THEN constants should match expected values
        assertEquals(BrawlerPowerLevel.MIN_VALUE, BrawlerPowerLevel.MIN.int)
        assertEquals(BrawlerPowerLevel.MAX_VALUE, BrawlerPowerLevel.MAX.int)
    }
}
