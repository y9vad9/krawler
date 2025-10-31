package krawler.server.player.application.test

import krawler.server.player.application.PlayerTag
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertNotNull

/**
 * Unit tests for [PlayerTag].
 */
class PlayerTagTest {

    @Test
    fun `isValid returns true for valid tags with and without #`() {
        val validTags = listOf("ABC123", "#XYZ789", "A1B2C3D4", "#123456")
        validTags.forEach { tag ->
            assertTrue(PlayerTag.isValid(tag), "Expected '$tag' to be valid")
        }
    }

    @Test
    fun `isValid returns false for invalid tags`() {
        val invalidTags = listOf(
            "ab",             // too short
            "A",              // too short
            "#a",             // too short
            "TOOLONGTAG123456789", // too long
            "abc$",           // invalid char
            "#abc$",          // invalid char
            "with space",     // invalid char
            ""                // empty
        )
        invalidTags.forEach { tag ->
            assertFalse(PlayerTag.isValid(tag), "Expected '$tag' to be invalid")
        }
    }

    @Test
    fun `create returns success for valid tag`() {
        val input = "PLAYER123"
        val result = PlayerTag.create(input)
        assertTrue(result.isSuccess)
        val tag = result.getOrNull()
        assertNotNull(tag)
        assertEquals("#$input", tag.stringWithTagPrefix)
        assertEquals(input, tag.stringWithoutTagPrefix)
    }

    @Test
    fun `create returns failure for invalid tag`() {
        val input = "!!INVALID"
        val result = PlayerTag.create(input)
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertIs<IllegalArgumentException>(exception)
        assertEquals("Invalid tag: $input", exception?.message)
    }

    @Test
    fun `createOrThrow returns PlayerTag for valid tag`() {
        val input = "VALID123"
        val tag = PlayerTag.createOrThrow(input)
        assertEquals("#$input", tag.stringWithTagPrefix)
    }

    @Test
    fun `createOrThrow throws IllegalArgumentException for invalid tag`() {
        val input = "badtag!"
        assertFailsWith<IllegalArgumentException> {
            val _ = PlayerTag.createOrThrow(input)
        }
    }

    @Test
    fun `createOrNull returns PlayerTag for valid tag`() {
        val input = "TAG456"
        val tag = PlayerTag.createOrNull(input)
        assertNotNull(tag)
        assertEquals("#$input", tag.stringWithTagPrefix)
    }

    @Test
    fun `createOrNull returns null for invalid tag`() {
        val input = "!!!"
        val tag = PlayerTag.createOrNull(input)
        assertNull(tag)
    }

    @Test
    fun `stringWithTagPrefix always returns string with #`() {
        val tag1 = PlayerTag.createOrThrow("ABC123")
        val tag2 = PlayerTag.createOrThrow("#XYZ789")
        assertEquals("#ABC123", tag1.stringWithTagPrefix)
        assertEquals("#XYZ789", tag2.stringWithTagPrefix)
    }

    @Test
    fun `stringWithoutTagPrefix always returns string without #`() {
        val tag1 = PlayerTag.createOrThrow("ABC123")
        val tag2 = PlayerTag.createOrThrow("#XYZ789")
        assertEquals("ABC123", tag1.stringWithoutTagPrefix)
        assertEquals("XYZ789", tag2.stringWithoutTagPrefix)
    }

    @Test
    fun `toString returns string with #`() {
        val tag = PlayerTag.createOrThrow("PLAYER1")
        assertEquals("#PLAYER1", tag.toString())
    }
}
