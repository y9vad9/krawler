package krawler.server.player.application.test

import krawler.server.player.application.PlayerHexColor
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class PlayerHexColorTest {

    @Test
    fun `isValid returns true for valid 6-digit hex with #`() {
        val validColors = listOf("#FF00FF", "#abcdef", "#123456", "#A1B2C3")
        validColors.forEach { color ->
            assertTrue(actual = PlayerHexColor.isValid(value = color), message = "Expected $color to be valid")
        }
    }

    @Test
    fun `isValid returns true for valid 3-digit hex with #`() {
        val validColors = listOf("#FFF", "#abc", "#123", "#A1B")
        validColors.forEach { color ->
            assertTrue(actual = PlayerHexColor.isValid(value = color), message = "Expected $color to be valid")
        }
    }

    @Test
    fun `isValid returns true for valid hex without #`() {
        val validColors = listOf("FF00FF", "abcdef", "123456", "FFF", "abc")
        validColors.forEach { color ->
            assertTrue(actual = PlayerHexColor.isValid(value = color), message = "Expected $color to be valid")
        }
    }

    @Test
    fun `isValid returns false for invalid hex strings`() {
        val invalidColors = listOf(
            "GHIJKL", "12", "1234", "12345G", "#12345G", "#FFFF", "#1234567", "", "ZZZ"
        )
        invalidColors.forEach { color ->
            assertFalse(actual = PlayerHexColor.isValid(value = color), message = "Expected $color to be invalid")
        }
    }

    @Test
    fun `create returns success for valid color`() {
        val input = "#ABCDEF"
        val result = PlayerHexColor.create(value = input)
        assertTrue(actual = result.isSuccess)
        assertEquals(expected = input, actual = result.getOrThrow().rawString)
    }

    @Test
    fun `create returns failure for invalid color`() {
        val input = "GHIJKL"
        val result = PlayerHexColor.create(value = input)
        assertTrue(actual = result.isFailure)
        assertTrue(actual = result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `createOrThrow returns instance for valid color`() {
        val input = "123456"
        val color = PlayerHexColor.createOrThrow(value = input)
        assertEquals(expected = input, actual = color.rawString)
    }

    @Test
    fun `createOrThrow throws for invalid color`() {
        val input = "XYZ"
        assertFailsWith<IllegalArgumentException> {
            val _ = PlayerHexColor.createOrThrow(value = input)
        }
    }

    @Test
    fun `createOrNull returns instance for valid color`() {
        val input = "#ABC"
        val color = PlayerHexColor.createOrNull(value = input)
        assertEquals(expected = input, actual = color?.rawString)
    }

    @Test
    fun `createOrNull returns null for invalid color`() {
        val input = "#12G"
        val color = PlayerHexColor.createOrNull(value = input)
        assertNull(actual = color)
    }
}
