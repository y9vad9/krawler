package krawler.server.player.application.test

import krawler.server.player.application.PlayerIconId
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class PlayerIconIdTest {

    @Test
    fun `isValid returns true for value equal or above MIN_VALUE`() {
        // GIVEN
        val validIds = listOf(
            PlayerIconId.MIN_VALUE,
            PlayerIconId.MIN_VALUE + 1,
            PlayerIconId.MIN_VALUE + 1000
        )

        // THEN
        validIds.forEach { id ->
            assertTrue(actual = PlayerIconId.isValid(input = id), message = "Expected $id to be valid")
        }
    }

    @Test
    fun `isValid returns false for value below MIN_VALUE`() {
        // GIVEN
        val invalidIds = listOf(
            PlayerIconId.MIN_VALUE - 1,
            PlayerIconId.MIN_VALUE - 100
        )

        // THEN
        invalidIds.forEach { id ->
            assertFalse(actual = PlayerIconId.isValid(input = id), message = "Expected $id to be invalid")
        }
    }

    @Test
    fun `create returns success for valid ID`() {
        // GIVEN
        val input = PlayerIconId.MIN_VALUE + 42

        // WHEN
        val result = PlayerIconId.create(input = input)

        // THEN
        assertTrue(actual = result.isSuccess)
        assertEquals(expected = input, actual = result.getOrThrow().raw)
    }

    @Test
    fun `create returns failure for invalid ID`() {
        // GIVEN
        val input = PlayerIconId.MIN_VALUE - 1

        // WHEN
        val result = PlayerIconId.create(input = input)

        // THEN
        assertTrue(actual = result.isFailure)
        assertTrue(actual = result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `createOrThrow returns PlayerIconId for valid input`() {
        // GIVEN
        val input = PlayerIconId.MIN_VALUE + 100

        // WHEN
        val iconId = PlayerIconId.createOrThrow(input = input)

        // THEN
        assertEquals(expected = input, actual = iconId.raw)
    }

    @Test
    fun `createOrThrow throws for invalid input`() {
        // GIVEN
        val input = PlayerIconId.MIN_VALUE - 50

        // THEN
        assertFailsWith<IllegalArgumentException> {
            val _ = PlayerIconId.createOrThrow(input = input)
        }
    }

    @Test
    fun `createOrNull returns PlayerIconId for valid input`() {
        // GIVEN
        val input = PlayerIconId.MIN_VALUE + 500

        // WHEN
        val iconId = PlayerIconId.createOrNull(input = input)

        // THEN
        assertEquals(expected = input, actual = iconId?.raw)
    }

    @Test
    fun `createOrNull returns null for invalid input`() {
        // GIVEN
        val input = PlayerIconId.MIN_VALUE - 1

        // WHEN
        val iconId = PlayerIconId.createOrNull(input = input)

        // THEN
        assertNull(actual = iconId)
    }

    @Test
    fun `compareTo compares raw values correctly`() {
        // GIVEN
        val lower = PlayerIconId.createOrThrow(input = PlayerIconId.MIN_VALUE)
        val higher = PlayerIconId.createOrThrow(input = PlayerIconId.MIN_VALUE + 10)

        // THEN
        assertTrue(actual = lower < higher)
        assertTrue(actual = higher > lower)
        assertEquals(expected = 0, actual = lower.compareTo(lower))
    }
}
