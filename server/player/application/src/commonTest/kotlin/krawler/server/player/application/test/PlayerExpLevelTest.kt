package krawler.server.player.application.test

import krawler.server.player.application.PlayerExpLevel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class PlayerExpLevelTest {

    @Test
    fun `isValid returns true for non-negative levels`() {
        // GIVEN a list of valid experience levels
        val validLevels = listOf(0, 1, 5, 100, Int.MAX_VALUE)

        // WHEN we check validity
        validLevels.forEach { level ->
            // THEN it should return true
            assertTrue(actual = PlayerExpLevel.isValid(input = level), message = "Expected $level to be valid")
        }
    }

    @Test
    fun `isValid returns false for negative levels`() {
        // GIVEN a list of negative experience levels
        val invalidLevels = listOf(-1, -10, Int.MIN_VALUE)

        // WHEN we check validity
        invalidLevels.forEach { level ->
            // THEN it should return false
            assertFalse(actual = PlayerExpLevel.isValid(input = level), message = "Expected $level to be invalid")
        }
    }

    @Test
    fun `create returns success for valid level`() {
        // GIVEN a valid experience level
        val input = 42

        // WHEN we create a PlayerExpLevel
        val result = PlayerExpLevel.create(input = input)

        // THEN it should succeed and wrap the correct value
        assertTrue(actual = result.isSuccess)
        assertEquals(expected = input, actual = result.getOrThrow().int)
    }

    @Test
    fun `create returns failure for invalid level`() {
        // GIVEN a negative experience level
        val input = -7

        // WHEN we try to create a PlayerExpLevel
        val result = PlayerExpLevel.create(input = input)

        // THEN it should fail with IllegalArgumentException
        assertTrue(actual = result.isFailure)
        assertTrue(actual = result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `createOrThrow returns instance for valid level`() {
        // GIVEN a valid experience level
        val input = 10

        // WHEN we call createOrThrow
        val level = PlayerExpLevel.createOrThrow(input = input)

        // THEN it should return a PlayerExpLevel with the correct value
        assertEquals(expected = input, actual = level.int)
    }

    @Test
    fun `createOrThrow throws for invalid level`() {
        // GIVEN a negative experience level
        val input = -1

        // WHEN/THEN it should throw IllegalArgumentException
        assertFailsWith<IllegalArgumentException> {
            val _ = PlayerExpLevel.createOrThrow(input = input)
        }
    }

    @Test
    fun `createOrNull returns instance for valid level`() {
        // GIVEN a valid experience level
        val input = 99

        // WHEN we call createOrNull
        val level = PlayerExpLevel.createOrNull(input = input)

        // THEN it should return a PlayerExpLevel with correct value
        assertEquals(expected = input, actual = level?.int)
    }

    @Test
    fun `createOrNull returns null for invalid level`() {
        // GIVEN a negative experience level
        val input = -100

        // WHEN we call createOrNull
        val level = PlayerExpLevel.createOrNull(input = input)

        // THEN it should return null
        assertNull(actual = level)
    }

    @Test
    fun `compareTo correctly compares levels`() {
        // GIVEN two experience levels
        val low = PlayerExpLevel.createOrThrow(input = 5)
        val high = PlayerExpLevel.createOrThrow(input = 10)

        // WHEN we compare them
        // THEN the comparison should reflect their numeric order
        assertTrue(actual = low < high)
        assertTrue(actual = high > low)
        assertEquals(expected = 0, actual = low.compareTo(low))
        assertEquals(expected = 0, actual = high.compareTo(high))
    }
}
