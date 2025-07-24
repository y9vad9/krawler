package com.y9vad9.brawlex.user.domain.test.value

import com.y9vad9.brawlex.user.domain.value.BrawlStarsPlayerTag
import com.y9vad9.brawlex.user.domain.value.BrawlStarsPlayerTag.CreationFailure
import com.y9vad9.valdi.ValidationResult
import com.y9vad9.valdi.getOrNull
import com.y9vad9.valdi.isSuccess
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class LinkedPlayerTagTest {

    @Test
    fun `creates tag with valid input without prefix`() {
        // GIVEN
        val input = "abc123"

        // WHEN
        val result = BrawlStarsPlayerTag.factory.create(input)

        // THEN
        assertTrue(
            actual = result.isSuccess(),
            message = "Expected success for input: $input",
        )
        val tag = result.getOrNull()
        assertNotNull(
            actual = tag,
            message = "Expected non-null tag for valid input",
        )
        assertEquals(
            expected = "#ABC123",
            actual = tag.stringWithTagPrefix,
            message = "Expected prefixed format to match",
        )
        assertEquals(
            expected = "ABC123",
            actual = tag.stringWithoutTagPrefix,
            message = "Expected normalized format to match",
        )
    }

    @Test
    fun `creates tag with valid input with prefix and lowercase`() {
        // GIVEN
        val input = "#ab9"

        // WHEN
        val result = BrawlStarsPlayerTag.factory.create(input)

        // THEN
        assertTrue(
            actual = result.isSuccess(),
            message = "Expected success for lowercase prefixed input: $input",
        )
        val tag = result.getOrNull()
        assertNotNull(
            actual = tag,
            message = "Expected tag to be non-null",
        )
        assertEquals(
            expected = "#AB9",
            actual = tag.stringWithTagPrefix,
            message = "Prefix format mismatch",
        )
        assertEquals(
            expected = "AB9",
            actual = tag.stringWithoutTagPrefix,
            message = "Normalized format mismatch",
        )
    }

    @Test
    fun `fails on input below minimum length`() {
        // GIVEN
        val input = "#ab"

        // WHEN
        val result = BrawlStarsPlayerTag.factory.create(input)

        // THEN
        assertIs<ValidationResult.Failure<*>>(
            value = result,
            message = "Expected failure for too short input",
        )
        assertIs<CreationFailure.TagNotWithinRangeFailure>(result.error)
    }

    @Test
    fun `fails on input above maximum length`() {
        // GIVEN
        val input = "#${"A".repeat(15)}"

        // WHEN
        val result = BrawlStarsPlayerTag.factory.create(input)

        // THEN
        assertIs<ValidationResult.Failure<*>>(
            value = result,
            message = "Expected failure for too long input",
        )
        assertIs<CreationFailure.TagNotWithinRangeFailure>(result.error)
    }

    @Test
    fun `fails on invalid format with special characters`() {
        // GIVEN
        val input = "#ABC@1"

        // WHEN
        val result = BrawlStarsPlayerTag.factory.create(input)

        // THEN
        assertIs<ValidationResult.Failure<*>>(
            value = result,
            message = "Expected failure for invalid characters",
        )
        assertIs<CreationFailure.InvalidFormatFailure>(result.error)
    }

    @Test
    fun `fails on whitespace-only input`() {
        // GIVEN
        val input = "   "

        // WHEN
        val result = BrawlStarsPlayerTag.factory.create(input)

        // THEN
        assertIs<ValidationResult.Failure<*>>(
            value = result,
            message = "Expected failure for whitespace input",
        )
        assertIs<CreationFailure.InvalidFormatFailure>(result.error)
    }

    @Test
    fun `stores and normalizes correctly with mixed case and no prefix`() {
        // GIVEN
        val input = "aBcDe1"

        // WHEN
        val result = BrawlStarsPlayerTag.factory.create(input)

        // THEN
        assertTrue(
            actual = result.isSuccess(),
            message = "Expected success for mixed-case input"
        )
        val tag = result.getOrNull()
        assertEquals(
            expected = "#ABCDE1",
            actual = tag?.stringWithTagPrefix,
            message = "Tag should normalize to uppercase with #"
        )
        assertEquals(
            expected = "ABCDE1",
            actual = tag?.stringWithoutTagPrefix,
            message = "Tag should normalize to uppercase without #",
        )
    }

    @Test
    fun `toString returns prefixed string`() {
        // GIVEN
        val input = "#abc123"

        // WHEN
        val tag = BrawlStarsPlayerTag.factory.create(input).getOrNull()

        // THEN
        assertEquals(
            expected = "#ABC123",
            actual = tag.toString(),
            message = "toString should return stringWithTagPrefix",
        )
    }
}
