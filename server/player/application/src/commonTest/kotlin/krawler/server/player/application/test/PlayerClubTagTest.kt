package krawler.server.player.application.test

import krawler.server.player.application.PlayerClubTag
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PlayerClubTagTest {

    // region — createOrThrow

    @Test
    fun `given valid tag without hash when created then should succeed and normalize`() {
        // Given
        val input = "abc123"

        // When
        val tag = PlayerClubTag.createOrThrow(input)

        // Then
        assertEquals("#ABC123", tag.stringWithTagPrefix)
        assertEquals("ABC123", tag.stringWithoutTagPrefix)
        assertEquals("#ABC123", tag.toString())
    }

    @Test
    fun `given valid tag with hash when created then should preserve prefix`() {
        // Given
        val input = "#XYZ987"

        // When
        val tag = PlayerClubTag.createOrThrow(input)

        // Then
        assertEquals("#XYZ987", tag.stringWithTagPrefix)
        assertEquals("XYZ987", tag.stringWithoutTagPrefix)
        assertEquals("#XYZ987", tag.toString())
    }

    @Test
    fun `given blank input when createdOrThrow then should throw with descriptive message`() {
        // Given
        val input = "   "

        // When & Then
        val exception = assertFailsWith<IllegalArgumentException> {
            val _ = PlayerClubTag.createOrThrow(input)
        }
        assertEquals("Club tag cannot be empty or blank.", exception.message)
    }

    @Test
    fun `given tag shorter than minimum length when createdOrThrow then should throw`() {
        // Given
        val input = "#A"

        // When & Then
        val exception = assertFailsWith<IllegalArgumentException> {
            val _ = PlayerClubTag.createOrThrow(input)
        }
        assertTrue(exception.message!!.contains("length must be between"))
    }

    @Test
    fun `given tag longer than maximum length when createdOrThrow then should throw`() {
        // Given
        val input = "#${"A".repeat(PlayerClubTag.MAX_LENGTH + 1)}"

        // When & Then
        val exception = assertFailsWith<IllegalArgumentException> {
            val _ = PlayerClubTag.createOrThrow(input)
        }
        assertTrue(exception.message!!.contains("length must be between"))
    }

    @Test
    fun `given tag with invalid characters when createdOrThrow then should throw`() {
        // Given
        val input = "#ABC$123"

        // When & Then
        val exception = assertFailsWith<IllegalArgumentException> {
            val _ = PlayerClubTag.createOrThrow(input)
        }
        assertTrue(exception.message!!.contains("may only contain A–Z and 0–9"))
    }

    // endregion

    // region — createOrNull

    @Test
    fun `given valid tag when createdOrNull then should return tag`() {
        // Given
        val input = "#QWE123"

        // When
        val tag = PlayerClubTag.createOrNull(input)

        // Then
        assertNotNull(tag)
        assertEquals("QWE123", tag.stringWithoutTagPrefix)
    }

    @Test
    fun `given invalid tag when createdOrNull then should return null`() {
        // Given
        val input = "#123$"

        // When
        val tag = PlayerClubTag.createOrNull(input)

        // Then
        assertNull(tag)
    }

    // endregion

    // region — create (FactoryResult)

    @Test
    fun `given blank input when create called then should return EmptyInput`() {
        // Given
        val input = ""

        // When
        val result = PlayerClubTag.create(input)

        // Then
        assertTrue(result is PlayerClubTag.FactoryResult.EmptyInput)
    }

    @Test
    fun `given invalid format when create called then should return InvalidFormat`() {
        // Given
        val input = "#ABC#123"

        // When
        val result = PlayerClubTag.create(input)

        // Then
        assertTrue(result is PlayerClubTag.FactoryResult.InvalidFormat)
    }

    @Test
    fun `given tag too short when create called then should return InvalidLength`() {
        // Given
        val input = "#A"

        // When
        val result = PlayerClubTag.create(input)

        // Then
        assertTrue(result is PlayerClubTag.FactoryResult.InvalidLength)
    }

    @Test
    fun `given valid tag when create called then should return Success with valid value`() {
        // Given
        val input = "#abc123"

        // When
        val result = PlayerClubTag.create(input)

        // Then
        assertTrue(result is PlayerClubTag.FactoryResult.Success)
        assertEquals("#ABC123", result.value.toString())
    }
    // endregion
}
