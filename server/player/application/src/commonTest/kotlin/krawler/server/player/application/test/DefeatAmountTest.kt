package krawler.server.player.application.test

import krawler.server.player.application.DefeatAmount
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Unit tests for [DefeatAmount].
 *
 * Verifies that creation, validation, comparison, and arithmetic operations
 * behave correctly for both valid and invalid victory counts.
 */
class DefeatAmountTest {

    @Test
    fun `given valid non-negative value when created then should hold the value`() {
        // Given
        val value = 8

        // When
        val defeats = DefeatAmount(value)

        // Then
        assertEquals(value, defeats.int, "DefeatAmount should store the provided non-negative value")
    }

    @Test
    fun `given negative value when created then should throw IllegalArgumentException`() {
        // Given
        val invalidValue = -5

        // When & Then
        val exception = assertFailsWith<IllegalArgumentException> {
            val _ = DefeatAmount(invalidValue)
        }
        assertEquals("Defeat amount must be zero or greater, got -5.", exception.message)
    }

    @Test
    fun `given two valid amounts when compared then should reflect correct ordering`() {
        // Given
        val smaller = DefeatAmount(2)
        val larger = DefeatAmount(9)

        // When
        val result = smaller < larger

        // Then
        assertEquals(true, result, "Smaller DefeatAmount should compare less than larger one")
    }

    @Test
    fun `given two valid amounts when added then should produce correct sum`() {
        // Given
        val first = DefeatAmount(4)
        val second = DefeatAmount(6)

        // When
        val result = first + second

        // Then
        assertEquals(DefeatAmount(10), result, "Addition should yield the correct total")
    }

    @Test
    fun `given two valid amounts when subtracted then should produce correct difference`() {
        // Given
        val first = DefeatAmount(10)
        val second = DefeatAmount(4)

        // When
        val result = first - second

        // Then
        assertEquals(DefeatAmount(6), result, "Subtraction should yield the correct non-negative result")
    }

    @Test
    fun `given subtraction resulting in negative value when performed then should throw`() {
        // Given
        val first = DefeatAmount(3)
        val second = DefeatAmount(9)

        // When & Then
        val exception = assertFailsWith<IllegalArgumentException> {
            val _ = first - second
        }
        assertEquals("Defeat amount must be zero or greater, got -6.", exception.message)
    }

    @Test
    fun `given amount when converted to string then should return its integer representation`() {
        // Given
        val defeats = DefeatAmount(11)

        // When
        val stringValue = defeats.toString()

        // Then
        assertEquals("11", stringValue, "toString should return the numeric string representation")
    }
}
