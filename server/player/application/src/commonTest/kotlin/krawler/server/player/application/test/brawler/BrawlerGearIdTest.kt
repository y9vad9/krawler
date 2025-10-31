package krawler.server.player.application.test.brawler

import krawler.server.player.application.brawler.BrawlerGearId
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BrawlerGearIdTest {

    @Test
    fun `isValid returns true for values within range`() {
        // GIVEN values inside the valid gear ID range
        val min = BrawlerGearId.MIN_VALUE
        val max = BrawlerGearId.MAX_VALUE

        // THEN isValid should return true
        assertTrue(BrawlerGearId.isValid(min))
        assertTrue(BrawlerGearId.isValid(max))
    }

    @Test
    fun `isValid returns false for values outside range`() {
        // GIVEN values outside the valid gear ID range
        val below = BrawlerGearId.MIN_VALUE - 1
        val above = BrawlerGearId.MAX_VALUE + 1

        // THEN isValid should return false
        assertFalse(BrawlerGearId.isValid(below))
        assertFalse(BrawlerGearId.isValid(above))
    }

    @Test
    fun `create returns success for valid input`() {
        // GIVEN a valid gear ID
        val input = BrawlerGearId.MIN_VALUE

        // THEN create should succeed
        val result = BrawlerGearId.create(input)
        assertTrue(result.isSuccess)
        assertEquals(input, result.getOrThrow().value)
    }

    @Test
    fun `create returns failure for invalid input`() {
        // GIVEN an invalid gear ID
        val input = BrawlerGearId.MIN_VALUE - 1

        // THEN create should fail
        val result = BrawlerGearId.create(input)
        assertTrue(result.isFailure)
    }

    @Test
    fun `createOrThrow returns instance for valid input`() {
        // GIVEN a valid gear ID
        val input = BrawlerGearId.MAX_VALUE

        // THEN createOrThrow should return an instance
        val id = BrawlerGearId.createOrThrow(input)
        assertEquals(input, id.value)
    }

    @Test
    fun `createOrThrow throws exception for invalid input`() {
        // GIVEN an invalid gear ID
        val input = BrawlerGearId.MAX_VALUE + 1

        // THEN createOrThrow should throw IllegalArgumentException
        assertFailsWith<IllegalArgumentException> {
            val _ = BrawlerGearId.createOrThrow(input)
        }
    }

    @Test
    fun `createOrNull returns instance for valid input`() {
        // GIVEN a valid gear ID
        val input = BrawlerGearId.MIN_VALUE

        // THEN createOrNull should return an instance
        val id = BrawlerGearId.createOrNull(input)
        assertEquals(input, id?.value)
    }

    @Test
    fun `createOrNull returns null for invalid input`() {
        // GIVEN an invalid gear ID
        val input = BrawlerGearId.MIN_VALUE - 1

        // THEN createOrNull should return null
        val id = BrawlerGearId.createOrNull(input)
        assertEquals(null, id)
    }

    @Test
    fun `compareTo returns correct ordering`() {
        // GIVEN two gear IDs
        val lower = BrawlerGearId.createOrThrow(BrawlerGearId.MIN_VALUE)
        val higher = BrawlerGearId.createOrThrow(BrawlerGearId.MAX_VALUE)

        // THEN compareTo should reflect numeric ordering
        assertTrue(lower < higher)
        assertTrue(higher > lower)
        assertEquals(0, lower.compareTo(BrawlerGearId.createOrThrow(BrawlerGearId.MIN_VALUE)))
    }
}
