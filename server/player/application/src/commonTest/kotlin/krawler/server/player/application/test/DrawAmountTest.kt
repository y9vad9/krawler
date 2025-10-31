package krawler.server.player.application.test

import krawler.server.player.application.DrawAmount
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for [DrawAmount].
 *
 * Verifies that creation, validation, comparison, and arithmetic operations
 * behave correctly for both valid and invalid victory counts.
 */
class DrawAmountTest {

    @Test
    fun `given non-negative input when isValid is called then returns true`() {
        // Given
        val input = 5

        // When
        val result = DrawAmount.isValid(input)

        // Then
        assertTrue(result)
    }

    @Test
    fun `given negative input when isValid is called then returns false`() {
        // Given
        val input = -1

        // When
        val result = DrawAmount.isValid(input)

        // Then
        assertFalse(result)
    }

    @Test
    fun `given valid input when create is called then returns success result`() {
        // Given
        val input = 10

        // When
        val result = DrawAmount.create(input)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(10, result.getOrThrow().int)
    }

    @Test
    fun `given invalid input when create is called then returns failure result`() {
        // Given
        val input = -10

        // When
        val result = DrawAmount.create(input)

        // Then
        assertTrue(result.isFailure)
        assertIs<IllegalArgumentException>(result.exceptionOrNull())
    }

    @Test
    fun `given valid input when createOrThrow is called then returns instance`() {
        // Given
        val input = 3

        // When
        val result = DrawAmount.createOrThrow(input)

        // Then
        assertEquals(3, result.int)
    }

    @Test
    fun `given invalid input when createOrThrow is called then throws exception`() {
        // Given
        val input = -3

        // When & Then
        assertFailsWith<IllegalArgumentException> {
            val _ = DrawAmount.createOrThrow(input)
        }
    }

    @Test
    fun `given valid input when createOrNull is called then returns instance`() {
        // Given
        val input = 7

        // When
        val result = DrawAmount.createOrNull(input)

        // Then
        assertNotNull(result)
        assertEquals(7, result.int)
    }

    @Test
    fun `given invalid input when createOrNull is called then returns null`() {
        // Given
        val input = -7

        // When
        val result = DrawAmount.createOrNull(input)

        // Then
        assertNull(result)
    }

    @Test
    fun `given two valid amounts when plus is called then returns sum`() {
        // Given
        val left = DrawAmount.createOrThrow(4)
        val right = DrawAmount.createOrThrow(6)

        // When
        val result = left + right

        // Then
        assertEquals(10, result.int)
    }

    @Test
    fun `given two valid amounts when minus results in non-negative value then returns difference`() {
        // Given
        val left = DrawAmount.createOrThrow(10)
        val right = DrawAmount.createOrThrow(4)

        // When
        val result = left - right

        // Then
        assertEquals(6, result.int)
    }

    @Test
    fun `given two valid amounts when minus results in negative value then throws exception`() {
        // Given
        val left = DrawAmount.createOrThrow(2)
        val right = DrawAmount.createOrThrow(5)

        // When & Then
        assertFailsWith<IllegalArgumentException> {
            val _ = left - right
        }
    }

    @Test
    fun `given two valid amounts when compareTo is called then returns correct ordering`() {
        // Given
        val smaller = DrawAmount.createOrThrow(3)
        val larger = DrawAmount.createOrThrow(7)

        // When & Then
        assertTrue(smaller < larger)
        assertTrue(larger > smaller)
    }

    @Test
    fun `given valid instance when toString is called then returns numeric string`() {
        // Given
        val drawAmount = DrawAmount.createOrThrow(42)

        // When
        val result = drawAmount.toString()

        // Then
        assertEquals("42", result)
    }
}
