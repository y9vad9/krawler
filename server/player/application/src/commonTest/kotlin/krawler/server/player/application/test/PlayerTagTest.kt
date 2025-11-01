package krawler.server.player.application.test

import krawler.server.player.application.PlayerTag
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for [PlayerTag].
 */
class PlayerTagTest {

    @Test
    fun `valid tag creates PlayerTag successfully`() {
        // Given a valid tag
        val raw = "#ABC123"

        // When creating a PlayerTag
        val result = PlayerTag.create(raw)

        // Then it should succeed
        assertTrue(
            actual = result is PlayerTag.FactoryResult.Success,
            message = "Valid tag should result in FactoryResult.Success"
        )

        val tag = result.value
        assertEquals(
            expected = "ABC123",
            actual = tag.stringWithoutTagPrefix,
            message = "Raw string should be normalized without '#'"
        )
        assertEquals(
            expected = "#ABC123",
            actual = tag.stringWithTagPrefix,
            message = "stringWithTagPrefix should start with '#'"
        )
    }

    @Test
    fun `empty tag returns Empty failure`() {
        // Given an empty string
        val raw = "   "

        // When creating
        val result = PlayerTag.create(raw)

        // Then
        assertTrue(
            actual = result is PlayerTag.FactoryResult.Failure.Empty,
            message = "Empty input should produce FactoryResult.Failure.Empty"
        )
    }

    @Test
    fun `too short tag returns TooShort failure`() {
        // Given a too short tag
        val raw = "#AB"

        // When creating
        val result = PlayerTag.create(raw)

        // Then
        assertTrue(
            actual = result is PlayerTag.FactoryResult.Failure.TooShort,
            message = "Tag shorter than MIN_LENGTH should produce FactoryResult.Failure.TooShort"
        )
        val length = result.length
        assertEquals(
            expected = 2,
            actual = length,
            message = "TooShort length should equal actual tag length"
        )
    }

    @Test
    fun `too long tag returns TooLong failure`() {
        // Given a too long tag
        val raw = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ"

        // When creating
        val result = PlayerTag.create(raw)

        // Then
        assertTrue(
            actual = result is PlayerTag.FactoryResult.Failure.TooLong,
            message = "Tag longer than MAX_LENGTH should produce FactoryResult.Failure.TooLong"
        )
    }

    @Test
    fun `tag with invalid characters returns InvalidCharacters failure`() {
        // Given a tag with invalid chars
        val raw = $$"#AB$C123"

        // When creating
        val result = PlayerTag.create(raw)

        // Then
        assertTrue(
            actual = result is PlayerTag.FactoryResult.Failure.InvalidCharacters,
            message = "Tag containing invalid characters should produce FactoryResult.Failure.InvalidCharacters"
        )
        val input = result.input
        assertEquals(
            expected = raw,
            actual = input,
            message = "InvalidCharacters input should match original tag"
        )
    }

    @Test
    fun `createOrThrow throws for invalid tag`() {
        // Given a too short tag
        val raw = "AB"

        // When / Then
        val ex = assertFailsWith<IllegalArgumentException> {
            val _ = PlayerTag.createOrThrow(raw)
        }

        assertContains(
            charSequence = ex.message ?: "",
            other = "too short",
            message = "Exception message should indicate tag is too short"
        )
    }

    @Test
    fun `createOrNull returns null for invalid tag`() {
        // Given invalid tag
        val raw = "A!"

        // When
        val result = PlayerTag.createOrNull(raw)

        // Then
        assertNull(
            actual = result,
            message = "createOrNull should return null for invalid input"
        )
    }
}
