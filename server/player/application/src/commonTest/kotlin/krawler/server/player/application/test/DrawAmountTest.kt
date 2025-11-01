package krawler.server.player.application.test

import krawler.server.player.application.DrawAmount
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Unit tests for [DrawAmount].
 *
 * Verifies that creation, validation, comparison, and arithmetic operations
 * behave correctly for both valid and invalid victory counts.
 */
class DrawAmountTest {

    @Test
    fun `given valid non-negative value when created then should hold the value`() {
        // Given
        val value = 5

        // When
        val draws = DrawAmount(value)

        // Then
        assertEquals(value, draws.int, "DrawAmount should store the provided non-negative value")
    }

    @Test
    fun `given negative value when created then should throw IllegalArgumentException`() {
        // Given
        val invalidValue = -3

        // When & Then
        val exception = assertFailsWith<IllegalArgumentException> {
            val _ = DrawAmount(invalidValue)
        }
        assertEquals("Draw amount must be zero or greater, got -3.", exception.message)
    }

    @Test
    fun `given two valid amounts when compared then should reflect correct ordering`() {
        // Given
        val smaller = DrawAmount(2)
        val larger = DrawAmount(7)

        // When
        val result = smaller < larger

        // Then
        assertEquals(true, result, "Smaller DrawAmount should compare less than larger one")
    }

    @Test
    fun `given two valid amounts when added then should produce correct sum`() {
        // Given
        val first = DrawAmount(4)
        val second = DrawAmount(6)

        // When
        val result = first + second

        // Then
        assertEquals(DrawAmount(10), result, "Addition should yield the correct total")
    }

    @Test
    fun `given two valid amounts when subtracted then should produce correct difference`() {
        // Given
        val first = DrawAmount(9)
        val second = DrawAmount(4)

        // When
        val result = first - second

        // Then
        assertEquals(DrawAmount(5), result, "Subtraction should yield the correct non-negative result")
    }

    @Test
    fun `given subtraction resulting in negative value when performed then should throw`() {
        // Given
        val first = DrawAmount(2)
        val second = DrawAmount(8)

        // When & Then
        val exception = assertFailsWith<IllegalArgumentException> {
            val _ = first - second
        }
        assertEquals("Draw amount must be zero or greater, got -6.", exception.message)
    }

    @Test
    fun `given amount when converted to string then should return its integer representation`() {
        // Given
        val draws = DrawAmount(11)

        // When
        val stringValue = draws.toString()

        // Then
        assertEquals("11", stringValue, "toString should return the numeric string representation")
    }
}
