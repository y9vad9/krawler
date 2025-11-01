package krawler.server.player.application.test

import krawler.server.player.application.PlayerHexColor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PlayerHexColorTest {

    // region: Valid inputs

    @Test
    fun `given valid 6-digit hex with # when created then should succeed`() {
        // Given
        val input = "#FF00AA"

        // When
        val color = PlayerHexColor(input)

        // Then
        assertEquals(input, color.rawString)
        assertEquals(input, color.toString())
    }

    @Test
    fun `given valid 3-digit hex without # when created then should succeed`() {
        // Given
        val input = "ABC"

        // When
        val color = PlayerHexColor(input)

        // Then
        assertEquals(input, color.rawString)
    }

    @Test
    fun `given lowercase hex with # when created then should succeed`() {
        // Given
        val input = "#ffcc00"

        // When
        val color = PlayerHexColor(input)

        // Then
        assertEquals(input, color.rawString)
    }

    // endregion

    // region: Invalid inputs

    @Test
    fun `given hex longer than 9 characters when created then should throw`() {
        // Given
        val input = "#123456789"

        // When & Then
        val ex = assertFailsWith<IllegalArgumentException> {
            val _ = PlayerHexColor(input)
        }

        assertTrue(ex.message!!.contains("at most 9 characters"))
    }

    @Test
    fun `given invalid hex format when created then should throw`() {
        // Given
        val input = "#GGHHII"

        // When & Then
        val ex = assertFailsWith<IllegalArgumentException> {
            val _ = PlayerHexColor(input)
        }

        assertTrue(ex.message!!.contains("Invalid HexColor format"))
    }

    @Test
    fun `given empty string when created then should throw`() {
        // Given
        val input = ""

        // When & Then
        val ex = assertFailsWith<IllegalArgumentException> {
            val _ = PlayerHexColor(input)
        }

        assertTrue(ex.message!!.contains("Invalid HexColor format"))
    }

    @Test
    fun `given invalid special characters when created then should throw`() {
        // Given
        val input = "#12-45*"

        // When & Then
        val ex = assertFailsWith<IllegalArgumentException> {
            val _ = PlayerHexColor(input)
        }

        assertTrue(ex.message!!.contains("Invalid HexColor format"))
    }

    // endregion
}
