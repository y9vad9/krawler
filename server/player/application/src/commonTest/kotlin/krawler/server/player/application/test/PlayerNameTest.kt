package krawler.server.player.application.test

import krawler.server.player.application.PlayerName
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class PlayerNameTest {

    @Test
    fun `isValid returns true for lengths within range`() {
        // GIVEN
        val validNames = listOf(
            "A", // min length
            "Player123",
            "💎✨🔥", // emojis
            "A".repeat(PlayerName.MAX_LENGTH) // max length
        )

        // THEN
        validNames.forEach { name ->
            assertTrue(actual = PlayerName.isValid(input = name), message = "Expected $name to be valid")
        }
    }

    @Test
    fun `isValid returns false for lengths out of range`() {
        // GIVEN
        val invalidNames = listOf(
            "", // too short
            "A".repeat(PlayerName.MAX_LENGTH + 1) // too long
        )

        // THEN
        invalidNames.forEach { name ->
            assertFalse(actual = PlayerName.isValid(input = name), message = "Expected $name to be invalid")
        }
    }

    @Test
    fun `create returns success for valid name`() {
        // GIVEN
        val input = "Brawler123"

        // WHEN
        val result = PlayerName.create(input = input)

        // THEN
        assertTrue(actual = result.isSuccess)
        assertEquals(expected = input, actual = result.getOrThrow().rawString)
    }

    @Test
    fun `create returns failure for invalid name`() {
        // GIVEN
        val input = "" // too short

        // WHEN
        val result = PlayerName.create(input = input)

        // THEN
        assertTrue(actual = result.isFailure)
        assertTrue(actual = result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `createOrThrow returns PlayerName for valid input`() {
        // GIVEN
        val input = "Champion"

        // WHEN
        val playerName = PlayerName.createOrThrow(input = input)

        // THEN
        assertEquals(expected = input, actual = playerName.rawString)
    }

    @Test
    fun `createOrThrow throws for invalid input`() {
        // GIVEN
        val input = "" // invalid

        // THEN
        assertFailsWith<IllegalArgumentException> {
            val _ = PlayerName.createOrThrow(input = input)
        }
    }

    @Test
    fun `createOrNull returns PlayerName for valid input`() {
        // GIVEN
        val input = "StarPlayer"

        // WHEN
        val playerName = PlayerName.createOrNull(input = input)

        // THEN
        assertEquals(expected = input, actual = playerName?.rawString)
    }

    @Test
    fun `createOrNull returns null for invalid input`() {
        // GIVEN
        val input = "" // invalid

        // WHEN
        val playerName = PlayerName.createOrNull(input = input)

        // THEN
        assertNull(actual = playerName)
    }

    @Test
    fun `toString returns rawString`() {
        // GIVEN
        val input = "Brawler"
        val playerName = PlayerName.createOrThrow(input = input)

        // THEN
        assertEquals(expected = input, actual = playerName.toString())
    }
}
