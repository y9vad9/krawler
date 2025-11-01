package krawler.server.player.application.test

import krawler.server.player.application.PlayerName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PlayerNameTest {

    // region: create()

    @Test
    fun `given valid name length within range when create called then returns Success`() {
        // Given
        val input = "Shelly"

        // When
        val result = PlayerName.create(input)

        // Then
        assertTrue(result is PlayerName.FactoryResult.Success)
        assertEquals(input, result.value.rawString)
    }

    @Test
    fun `given name shorter than MIN_LENGTH when create called then returns TooShort`() {
        // Given
        val input = "" // empty string

        // When
        val result = PlayerName.create(input)

        // Then
        assertTrue(result is PlayerName.FactoryResult.TooShort)
    }

    @Test
    fun `given name longer than MAX_LENGTH when create called then returns TooLong`() {
        // Given
        val input = "A".repeat(PlayerName.MAX_LENGTH + 1)

        // When
        val result = PlayerName.create(input)

        // Then
        assertTrue(result is PlayerName.FactoryResult.TooLong)
    }

    // endregion

    // region: createOrThrow()

    @Test
    fun `given valid name when createOrThrow called then returns PlayerName`() {
        // Given
        val input = "Colt"

        // When
        val name = PlayerName.createOrThrow(input)

        // Then
        assertEquals(input, name.rawString)
    }

    @Test
    fun `given too short name when createOrThrow called then throws`() {
        // Given
        val input = ""

        // When & Then
        val ex = assertFailsWith<IllegalArgumentException> {
            val _ = PlayerName.createOrThrow(input)
        }
        assertTrue(ex.message!!.contains("at least"))
    }

    @Test
    fun `given too long name when createOrThrow called then throws`() {
        // Given
        val input = "B".repeat(PlayerName.MAX_LENGTH + 5)

        // When & Then
        val ex = assertFailsWith<IllegalArgumentException> {
            val _ = PlayerName.createOrThrow(input)
        }
        assertTrue(ex.message!!.contains("at most"))
    }

    // endregion

    // region: createOrNull()

    @Test
    fun `given valid name when createOrNull called then returns PlayerName`() {
        // Given
        val input = "Piper"

        // When
        val name = PlayerName.createOrNull(input)

        // Then
        assertNotNull(name)
        assertEquals(input, name.rawString)
    }

    @Test
    fun `given invalid name when createOrNull called then returns null`() {
        // Given
        val input = "" // too short

        // When
        val name = PlayerName.createOrNull(input)

        // Then
        assertNull(name)
    }

    // endregion

    // region: toString()

    @Test
    fun `toString returns raw player name`() {
        // Given
        val input = "Crow"
        val name = PlayerName.createOrThrow(input)

        // When
        val result = name.toString()

        // Then
        assertEquals(input, result)
    }

    // endregion
}
