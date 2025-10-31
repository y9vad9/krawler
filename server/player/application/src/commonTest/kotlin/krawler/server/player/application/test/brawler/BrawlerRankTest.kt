package krawler.server.player.application.test.brawler

import krawler.server.player.application.brawler.BrawlerRank
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BrawlerRankTest {

    @Test
    fun `isValid returns true for values within valid range`() {
        // GIVEN values inside the valid brawler rank range
        val min = BrawlerRank.MIN.raw
        val max = BrawlerRank.MAX.raw

        // THEN isValid should return true
        assertTrue(BrawlerRank.isValid(min))
        assertTrue(BrawlerRank.isValid(max))
        assertTrue(BrawlerRank.isValid(25))
    }

    @Test
    fun `isValid returns false for values outside valid range`() {
        // GIVEN values outside the valid brawler rank range
        val below = BrawlerRank.MIN.raw - 1
        val above = BrawlerRank.MAX.raw + 1

        // THEN isValid should return false
        assertFalse(BrawlerRank.isValid(below))
        assertFalse(BrawlerRank.isValid(above))
    }

    @Test
    fun `create returns success for valid input`() {
        // GIVEN a valid input
        val input = 10

        // THEN create should succeed
        val result = BrawlerRank.create(input)
        assertTrue(result.isSuccess)
        assertEquals(input, result.getOrThrow().raw)
    }

    @Test
    fun `create returns failure for invalid input`() {
        // GIVEN an invalid input
        val input = 100

        // THEN create should fail
        val result = BrawlerRank.create(input)
        assertTrue(result.isFailure)
    }

    @Test
    fun `createOrThrow returns instance for valid input`() {
        // GIVEN a valid input
        val input = BrawlerRank.MIN.raw

        // THEN createOrThrow should return a valid instance
        val rank = BrawlerRank.createOrThrow(input)
        assertEquals(input, rank.raw)
    }

    @Test
    fun `createOrThrow throws exception for invalid input`() {
        // GIVEN an invalid input
        val input = 0

        // THEN createOrThrow should throw IllegalArgumentException
        assertFailsWith<IllegalArgumentException> {
            val _ = BrawlerRank.createOrThrow(input)
        }
    }

    @Test
    fun `createOrNull returns instance for valid input`() {
        // GIVEN a valid input
        val input = 20

        // THEN createOrNull should return a valid instance
        val rank = BrawlerRank.createOrNull(input)
        assertEquals(input, rank?.raw)
    }

    @Test
    fun `createOrNull returns null for invalid input`() {
        // GIVEN an invalid input
        val input = 52

        // THEN createOrNull should return null
        val rank = BrawlerRank.createOrNull(input)
        assertEquals(null, rank)
    }

    @Test
    fun `compareTo reflects numeric ordering correctly`() {
        // GIVEN two ranks created through factory
        val lower = BrawlerRank.createOrThrow(5)
        val higher = BrawlerRank.createOrThrow(30)

        // THEN compareTo should reflect numeric ordering
        assertTrue(lower < higher)
        assertTrue(higher > lower)
        assertEquals(0, lower.compareTo(BrawlerRank.createOrThrow(5)))
    }

    @Test
    fun `predefined constants MIN and MAX have correct raw values`() {
        // THEN constants should match expected values
        assertEquals(1, BrawlerRank.MIN.raw)
        assertEquals(51, BrawlerRank.MAX.raw)
    }
}
