package krawler.server.player.application.test

import krawler.server.player.application.Trophies
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertIs

/**
 * Unit tests for [Trophies].
 *
 * Ensures consistency across validation and factory methods,
 * as well as correct behavior of arithmetic and comparison operations.
 */
class TrophiesTest {

    @Test
    fun `given valid value when isValid called then returns true`() {
        // Given
        val value = 100

        // When
        val result = Trophies.isValid(value)

        // Then
        assertTrue(result)
    }

    @Test
    fun `given invalid value when isValid called then returns false`() {
        // Given
        val value = -5

        // When
        val result = Trophies.isValid(value)

        // Then
        assertFalse(result)
    }

    @Test
    fun `given valid value when create called then returns success`() {
        // Given
        val value = 50

        // When
        val result = Trophies.create(value)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(50, result.getOrThrow().rawInt)
    }

    @Test
    fun `given invalid value when create called then returns failure`() {
        // Given
        val value = -10

        // When
        val result = Trophies.create(value)

        // Then
        assertTrue(result.isFailure)
        assertIs<IllegalArgumentException>(result.exceptionOrNull())
    }

    @Test
    fun `given valid value when createOrThrow called then returns instance`() {
        // Given
        val value = 77

        // When
        val trophies = Trophies.createOrThrow(value)

        // Then
        assertEquals(77, trophies.rawInt)
    }

    @Test
    fun `given invalid value when createOrThrow called then throws exception`() {
        // Given
        val value = -1

        // When & Then
        assertFailsWith<IllegalArgumentException> {
            val _ = Trophies.createOrThrow(value)
        }
    }

    @Test
    fun `given valid value when createOrNull called then returns instance`() {
        // Given
        val value = 33

        // When
        val trophies = Trophies.createOrNull(value)

        // Then
        assertNotNull(trophies)
        assertEquals(33, trophies.rawInt)
    }

    @Test
    fun `given invalid value when createOrNull called then returns null`() {
        // Given
        val value = -33

        // When
        val trophies = Trophies.createOrNull(value)

        // Then
        assertNull(trophies)
    }

    @Test
    fun `given same input all creation methods are consistent`() {
        // Given
        val value = 25

        // When
        val isValid = Trophies.isValid(value)
        val createResult = Trophies.create(value)
        val createOrNull = Trophies.createOrNull(value)
        val createOrThrow = Trophies.createOrThrow(value)

        // Then
        assertTrue(isValid)
        assertTrue(createResult.isSuccess)
        assertNotNull(createOrNull)
        assertEquals(createOrNull, createOrThrow)
    }

    @Test
    fun `given two Trophies when plus is called then returns sum`() {
        // Given
        val t1 = Trophies.createOrThrow(10)
        val t2 = Trophies.createOrThrow(15)

        // When
        val result = t1 + t2

        // Then
        assertEquals(25, result.rawInt)
    }

    @Test
    fun `given two Trophies when compareTo called then returns correct ordering`() {
        // Given
        val smaller = Trophies.createOrThrow(5)
        val larger = Trophies.createOrThrow(20)

        // Then
        assertTrue(smaller < larger)
        assertTrue(larger > smaller)
    }

    @Test
    fun `given Trophies when toString called then returns numeric string`() {
        // Given
        val trophies = Trophies.createOrThrow(123)

        // When
        val str = trophies.toString()

        // Then
        assertEquals("123", str)
    }
}
