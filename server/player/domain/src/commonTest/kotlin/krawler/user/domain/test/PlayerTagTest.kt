package krawler.user.domain.test

import krawler.user.domain.PlayerTag
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PlayerTagTest {

    // region — Successful creation tests

    @Test
    fun `creates tag with valid input without prefix`() {
        // GIVEN
        val input = "abc123"

        // WHEN
        val result = PlayerTag.create(input)

        // THEN
        assertIs<PlayerTag.FactoryResult.Success>(result)
        val tag = result.value
        assertEquals("#ABC123", tag.stringWithTagPrefix)
        assertEquals("ABC123", tag.stringWithoutTagPrefix)
    }

    @Test
    fun `creates tag with valid input with prefix and lowercase`() {
        // GIVEN
        val input = "#ab9"

        // WHEN
        val result = PlayerTag.create(input)

        // THEN
        assertIs<PlayerTag.FactoryResult.Success>(result)
        val tag = result.value
        assertEquals("#AB9", tag.stringWithTagPrefix)
        assertEquals("AB9", tag.stringWithoutTagPrefix)
    }

    @Test
    fun `stores and normalizes correctly with mixed case and no prefix`() {
        // GIVEN
        val input = "aBcDe1"

        // WHEN
        val result = PlayerTag.create(input)

        // THEN
        assertIs<PlayerTag.FactoryResult.Success>(result)
        val tag = result.value
        assertEquals("#ABCDE1", tag.stringWithTagPrefix)
        assertEquals("ABCDE1", tag.stringWithoutTagPrefix)
    }

    @Test
    fun `toString returns prefixed string`() {
        // GIVEN
        val input = "#abc123"

        // WHEN
        val tag = PlayerTag.createOrNull(input)

        // THEN
        assertEquals("#ABC123", tag.toString())
    }

    // endregion

    // region — Failure creation tests

    @Test
    fun `fails on input below minimum length`() {
        // GIVEN
        val input = "#ab"

        // WHEN
        val result = PlayerTag.create(input)

        // THEN
        assertIs<PlayerTag.FactoryResult.TagNotWithinRange>(result)
    }

    @Test
    fun `fails on input above maximum length`() {
        // GIVEN
        val input = "#${"A".repeat(15)}"

        // WHEN
        val result = PlayerTag.create(input)

        // THEN
        assertIs<PlayerTag.FactoryResult.TagNotWithinRange>(result)
    }

    @Test
    fun `fails on invalid format with special characters`() {
        // GIVEN
        val input = "#ABC@1"

        // WHEN
        val result = PlayerTag.create(input)

        // THEN
        assertIs<PlayerTag.FactoryResult.InvalidFormat>(result)
    }

    @Test
    fun `fails on whitespace-only input`() {
        // GIVEN
        val input = "   "

        // WHEN
        val result = PlayerTag.create(input)

        // THEN
        assertIs<PlayerTag.FactoryResult.InvalidFormat>(result)
    }

    // endregion

    // region — Consistency tests for createOrNull and createOrThrow

    @Test
    fun `createOrNull returns tag for valid input`() {
        // GIVEN
        val input = "#abc123"

        // WHEN
        val tag = PlayerTag.createOrNull(input)

        // THEN
        assertNotNull(tag)
        assertEquals("#ABC123", tag.stringWithTagPrefix)
    }

    @Test
    fun `createOrNull returns null for invalid input`() {
        // GIVEN
        val input = "#@@@"

        // WHEN
        val tag = PlayerTag.createOrNull(input)

        // THEN
        assertNull(tag)
    }

    @Test
    fun `createOrThrow returns tag for valid input`() {
        // GIVEN
        val input = "abc123"

        // WHEN
        val tag = PlayerTag.createOrThrow(input)

        // THEN
        assertEquals("#ABC123", tag.stringWithTagPrefix)
    }

    @Test
    fun `createOrThrow throws for invalid input`() {
        // GIVEN
        val input = "#abc@"

        // THEN
        val exception = assertFailsWith<IllegalArgumentException> {
            @Suppress("RETURN_VALUE_NOT_USED")
            PlayerTag.createOrThrow(input)
        }
        assertTrue(exception.message!!.contains("returned"))
    }

    // endregion
}
