package krawler.server.player.application.test

import krawler.server.player.application.PlayerExpLevel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PlayerExpLevelTest {

    @Test
    fun `given non-negative value when created then should construct successfully`() {
        // Given
        val value = 10

        // When
        val level = PlayerExpLevel(value)

        // Then
        assertEquals(value, level.int)
    }

    @Test
    fun `given zero value when created then should construct successfully`() {
        // Given
        val value = 0

        // When
        val level = PlayerExpLevel(value)

        // Then
        assertEquals(value, level.int)
    }

    @Test
    fun `given negative value when created then should throw IllegalArgumentException`() {
        // Given
        val value = -1

        // When & Then
        val exception = assertFailsWith<IllegalArgumentException> {
            val _ = PlayerExpLevel(value)
        }
        assertEquals("Player experience level must be zero or greater.", exception.message)
    }

    @Test
    fun `given two levels when compared then should return correct ordering`() {
        // Given
        val low = PlayerExpLevel(5)
        val high = PlayerExpLevel(10)

        // When & Then
        assertTrue(low < high)
        assertTrue(high > low)
        assertTrue(low < high)
        assertTrue(high > low)
        assertEquals(0, PlayerExpLevel(7).compareTo(PlayerExpLevel(7)))
    }
}
