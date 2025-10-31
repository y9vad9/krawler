package krawler.server.player.application.test

import krawler.server.player.application.PlayerClubTag
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class PlayerClubTagTest {

    @Test
    fun `isValid returns true for valid club tags`() {
        // GIVEN a list of valid club tags
        val validTags = listOf(
            "ABC", "#DEF123", "GHI7890", "JKLMN", "#12345678901234"
        )

        // WHEN we check validity
        validTags.forEach { tag ->
            // THEN it should return true
            assertTrue(actual = PlayerClubTag.isValid(input = tag), message = "Expected $tag to be valid")
        }
    }

    @Test
    fun `isValid returns false for invalid club tags`() {
        // GIVEN a list of invalid club tags
        val invalidTags = listOf(
            "AB",                 // too short
            "A".repeat(15),       // too long
            "#abc_def",           // invalid character
            "!!@@##",             // invalid characters
            "a b c",              // spaces
        )

        // WHEN we check validity
        invalidTags.forEach { tag ->
            // THEN it should return false
            assertFalse(actual = PlayerClubTag.isValid(input = tag), message = "Expected $tag to be invalid")
        }
    }

    @Test
    fun `create returns success for valid tags`() {
        // GIVEN a valid club tag
        val input = "abc123"

        // WHEN we create a PlayerClubTag
        val result = PlayerClubTag.create(input = input)

        // THEN it should succeed and normalize the string to uppercase
        assertTrue(actual = result.isSuccess)
        assertEquals(expected = "#ABC123", actual = result.getOrThrow().stringWithTagPrefix)
    }

    @Test
    fun `create returns failure for invalid tags`() {
        // GIVEN an invalid club tag
        val input = "ab"

        // WHEN we attempt to create a PlayerClubTag
        val result = PlayerClubTag.create(input = input)

        // THEN it should fail
        assertTrue(actual = result.isFailure)
        assertTrue(actual = result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `createOrThrow returns instance for valid tag`() {
        // GIVEN a valid club tag
        val input = "xyz789"

        // WHEN we call createOrThrow
        val tag = PlayerClubTag.createOrThrow(input = input)

        // THEN it should return a PlayerClubTag with normalized uppercase and # prefix
        assertEquals(expected = "#XYZ789", actual = tag.stringWithTagPrefix)
        assertEquals(expected = "XYZ789", actual = tag.stringWithoutTagPrefix)
    }

    @Test
    fun `createOrThrow throws for invalid tag`() {
        // GIVEN an invalid club tag
        val input = "!!"

        // WHEN/THEN it should throw IllegalArgumentException
        assertFailsWith<IllegalArgumentException> {
            val _ = PlayerClubTag.createOrThrow(input = input)
        }
    }

    @Test
    fun `createOrNull returns instance for valid tag`() {
        // GIVEN a valid club tag
        val input = "klm456"

        // WHEN we call createOrNull
        val tag = PlayerClubTag.createOrNull(input = input)

        // THEN it should return a PlayerClubTag instance
        assertEquals(expected = "#KLM456", actual = tag?.stringWithTagPrefix)
    }

    @Test
    fun `createOrNull returns null for invalid tag`() {
        // GIVEN an invalid club tag
        val input = "12"

        // WHEN we call createOrNull
        val tag = PlayerClubTag.createOrNull(input = input)

        // THEN it should return null
        assertNull(actual = tag)
    }
}
