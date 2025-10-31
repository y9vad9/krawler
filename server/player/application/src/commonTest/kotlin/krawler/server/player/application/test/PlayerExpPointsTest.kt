package krawler.server.player.application.test

import krawler.server.player.application.PlayerExpPoints
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class PlayerExpPointsTest {

    @Test
    fun `isValid returns true for non-negative XP`() {
        // GIVEN a list of non-negative XP values
        val validXps = listOf(0, 1, 50, 9999, Int.MAX_VALUE)

        // WHEN we check validity
        validXps.forEach { xp ->
            // THEN it should return true
            assertTrue(actual = PlayerExpPoints.isValid(input = xp), message = "Expected $xp to be valid")
        }
    }

    @Test
    fun `isValid returns false for negative XP`() {
        // GIVEN a list of negative XP values
        val invalidXps = listOf(-1, -50, -999, Int.MIN_VALUE)

        // WHEN we check validity
        invalidXps.forEach { xp ->
            // THEN it should return false
            assertFalse(actual = PlayerExpPoints.isValid(input = xp), message = "Expected $xp to be invalid")
        }
    }

    @Test
    fun `create returns success for valid XP`() {
        // GIVEN a valid XP value
        val input = 100

        // WHEN we create a PlayerExpPoints
        val result = PlayerExpPoints.create(input = input)

        // THEN it should succeed and wrap the correct value
        assertTrue(actual = result.isSuccess)
        assertEquals(expected = input, actual = result.getOrThrow().int)
    }

    @Test
    fun `create returns failure for invalid XP`() {
        // GIVEN a negative XP value
        val input = -10

        // WHEN we try to create a PlayerExpPoints
        val result = PlayerExpPoints.create(input = input)

        // THEN it should fail with IllegalArgumentException
        assertTrue(actual = result.isFailure)
        assertTrue(actual = result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `createOrThrow returns instance for valid XP`() {
        // GIVEN a valid XP value
        val input = 500

        // WHEN we call createOrThrow
        val xp = PlayerExpPoints.createOrThrow(input = input)

        // THEN it should return a PlayerExpPoints with the correct value
        assertEquals(expected = input, actual = xp.int)
    }

    @Test
    fun `createOrThrow throws for invalid XP`() {
        // GIVEN a negative XP value
        val input = -1

        // WHEN/THEN it should throw IllegalArgumentException
        assertFailsWith<IllegalArgumentException> {
            val _ = PlayerExpPoints.createOrThrow(input = input)
        }
    }

    @Test
    fun `createOrNull returns instance for valid XP`() {
        // GIVEN a valid XP value
        val input = 250

        // WHEN we call createOrNull
        val xp = PlayerExpPoints.createOrNull(input = input)

        // THEN it should return a PlayerExpPoints with correct value
        assertEquals(expected = input, actual = xp?.int)
    }

    @Test
    fun `createOrNull returns null for invalid XP`() {
        // GIVEN a negative XP value
        val input = -100

        // WHEN we call createOrNull
        val xp = PlayerExpPoints.createOrNull(input = input)

        // THEN it should return null
        assertNull(actual = xp)
    }

    @Test
    fun `compareTo correctly compares XP values`() {
        // GIVEN two XP values
        val low = PlayerExpPoints.createOrThrow(input = 100)
        val high = PlayerExpPoints.createOrThrow(input = 200)

        // WHEN we compare them
        // THEN the comparison should reflect their numeric order
        assertTrue(actual = low < high)
        assertTrue(actual = high > low)
        assertEquals(expected = 0, actual = low.compareTo(low))
        assertEquals(expected = 0, actual = high.compareTo(high))
    }
}
