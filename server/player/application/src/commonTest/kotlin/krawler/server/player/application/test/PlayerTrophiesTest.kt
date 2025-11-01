package krawler.server.player.application.test

import krawler.server.player.application.PlayerTrophies
import krawler.server.player.application.Trophies
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Unit tests for [PlayerTrophies].
 */

class PlayerTrophiesTest {

    @Test
    fun `creating PlayerTrophies with valid current and highest succeeds`() {
        // Given a current and highest trophy count
        val current = Trophies(500)
        val highest = Trophies(1000)

        // When constructing PlayerTrophies
        val playerTrophies = PlayerTrophies(current = current, highest = highest)

        // Then
        assertEquals(
            expected = current,
            actual = playerTrophies.current,
            message = "PlayerTrophies.current should store the current trophy value"
        )
        assertEquals(
            expected = highest,
            actual = playerTrophies.highest,
            message = "PlayerTrophies.highest should store the highest trophy value"
        )
    }

    @Test
    fun `creating PlayerTrophies with current greater than highest throws`() {
        // Given an invalid current higher than highest
        val current = Trophies(1500)
        val highest = Trophies(1000)

        // When / Then
        val ex = assertFailsWith<IllegalArgumentException> {
            val _ = PlayerTrophies(current = current, highest = highest)
        }
        assertContains(
            charSequence = ex.message ?: "",
            other = "Player highest can't be less than current",
            message = "Exception message should indicate current exceeds highest"
        )
    }

    @Test
    fun `creating PlayerTrophies with current equal to highest succeeds`() {
        // Given current equal to highest
        val value = Trophies(800)

        // When constructing PlayerTrophies
        val playerTrophies = PlayerTrophies(current = value, highest = value)

        // Then
        assertEquals(
            expected = value,
            actual = playerTrophies.current,
            message = "Current and highest can be equal"
        )
        assertEquals(
            expected = value,
            actual = playerTrophies.highest,
            message = "Current and highest can be equal"
        )
    }
}
