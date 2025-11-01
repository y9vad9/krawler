package krawler.server.player.application.test

import krawler.server.player.application.PlayerExpPoints
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PlayerExpPointsTest {

    @Test
    fun `given non-negative value when created then should construct successfully`() {
        // Given
        val value = 1500

        // When
        val xp = PlayerExpPoints(value)

        // Then
        assertEquals(value, xp.int)
    }

    @Test
    fun `given zero value when created then should construct successfully`() {
        // Given
        val value = 0

        // When
        val xp = PlayerExpPoints(value)

        // Then
        assertEquals(value, xp.int)
    }

    @Test
    fun `given negative value when created then should throw IllegalArgumentException`() {
        // Given
        val value = -42

        // When & Then
        val exception = assertFailsWith<IllegalArgumentException> {
            val _ = PlayerExpPoints(value)
        }
        assertEquals(
            "Player experience points must be zero or greater, but was -42.",
            exception.message
        )
    }

    @Test
    fun `given two XP values when compared then should order by numeric value`() {
        // Given
        val low = PlayerExpPoints(500)
        val high = PlayerExpPoints(1200)

        // When & Then
        assertTrue(low < high)
        assertTrue(high > low)
        assertEquals(0, PlayerExpPoints(750).compareTo(PlayerExpPoints(750)))
    }

    @Test
    fun `given XP value when converted to string then should return numeric representation`() {
        // Given
        val xp = PlayerExpPoints(2300)

        // When
        val stringValue = xp.toString()

        // Then
        assertEquals("2300", stringValue)
    }
}
