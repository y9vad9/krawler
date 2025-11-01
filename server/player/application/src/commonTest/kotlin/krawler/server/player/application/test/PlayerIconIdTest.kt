package krawler.server.player.application.test

import krawler.server.player.application.PlayerIconId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PlayerIconIdTest {

    // region: Valid construction

    @Test
    fun `given valid id equal to MIN_VALUE when created then succeeds`() {
        // Given
        val input = PlayerIconId.MIN_VALUE

        // When
        val iconId = PlayerIconId(input)

        // Then
        assertEquals(input, iconId.raw)
        assertEquals(input.toString(), iconId.toString())
    }

    @Test
    fun `given id greater than MIN_VALUE when created then succeeds`() {
        // Given
        val input = PlayerIconId.MIN_VALUE + 10

        // When
        val iconId = PlayerIconId(input)

        // Then
        assertEquals(input, iconId.raw)
    }

    // endregion

    // region: Invalid construction

    @Test
    fun `given id less than MIN_VALUE when created then throws`() {
        // Given
        val input = PlayerIconId.MIN_VALUE - 1

        // When & Then
        val ex = assertFailsWith<IllegalArgumentException> {
            val _ = PlayerIconId(input)
        }

        assertTrue(ex.message!!.contains("greater than or equal to"))
    }

    // endregion

    // region: Comparison

    @Test
    fun `given two PlayerIconIds when compared then returns correct ordering`() {
        // Given
        val lower = PlayerIconId(PlayerIconId.MIN_VALUE)
        val higher = PlayerIconId(PlayerIconId.MIN_VALUE + 1)

        // When & Then
        assertTrue(lower < higher)
        assertTrue(higher > lower)
        assertEquals(0, lower.compareTo(PlayerIconId(PlayerIconId.MIN_VALUE)))
    }

    // endregion

    // region: String representation

    @Test
    fun `toString returns string representation of raw value`() {
        // Given
        val id = PlayerIconId(28_000_123)

        // When
        val result = id.toString()

        // Then
        assertEquals("28000123", result)
    }

    // endregion
}
