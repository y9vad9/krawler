package krawler.server.auth.domain.test

import krawler.server.auth.domain.ChallengedPlayerTag
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ChallengedPlayerTagTest {

    // region — Core factory tests

    @Test
    fun `creates tag with valid input without prefix`() {
        // GIVEN
        val input = "abc123"

        // WHEN
        val result = ChallengedPlayerTag.create(input)

        // THEN
        assertIs<ChallengedPlayerTag.FactoryResult.Success>(
            value = result,
            message = "Expected success for input: $input",
        )
        val tag = result.value
        assertNotNull(tag)
        assertEquals("#ABC123", tag.stringWithTagPrefix)
        assertEquals("ABC123", tag.stringWithoutTagPrefix)
    }

    @Test
    fun `creates tag with valid input with prefix and lowercase`() {
        // GIVEN
        val input = "#ab9"

        // WHEN
        val result = ChallengedPlayerTag.create(input)

        // THEN
        assertIs<ChallengedPlayerTag.FactoryResult.Success>(result)
        val tag = result.value
        assertNotNull(tag)
        assertEquals("#AB9", tag.stringWithTagPrefix)
        assertEquals("AB9", tag.stringWithoutTagPrefix)
    }

    @Test
    fun `fails on input below minimum length`() {
        // GIVEN
        val input = "#ab"

        // WHEN
        val result = ChallengedPlayerTag.create(input)

        // THEN
        assertIs<ChallengedPlayerTag.FactoryResult.TagNotWithinRange>(result)
    }

    @Test
    fun `fails on input above maximum length`() {
        // GIVEN
        val input = "#${"A".repeat(15)}"

        // WHEN
        val result = ChallengedPlayerTag.create(input)

        // THEN
        assertIs<ChallengedPlayerTag.FactoryResult.TagNotWithinRange>(result)
    }

    @Test
    fun `fails on invalid format with special characters`() {
        // GIVEN
        val input = "#ABC@1"

        // WHEN
        val result = ChallengedPlayerTag.create(input)

        // THEN
        assertIs<ChallengedPlayerTag.FactoryResult.InvalidFormat>(result)
    }

    @Test
    fun `fails on whitespace-only input`() {
        // GIVEN
        val input = "   "

        // WHEN
        val result = ChallengedPlayerTag.create(input)

        // THEN
        assertIs<ChallengedPlayerTag.FactoryResult.InvalidFormat>(result)
    }

    @Test
    fun `stores and normalizes correctly with mixed case and no prefix`() {
        // GIVEN
        val input = "aBcDe1"

        // WHEN
        val result = ChallengedPlayerTag.create(input)

        // THEN
        assertIs<ChallengedPlayerTag.FactoryResult.Success>(result)
        val tag = result.value
        assertEquals("#ABCDE1", tag.stringWithTagPrefix)
        assertEquals("ABCDE1", tag.stringWithoutTagPrefix)
    }

    @Test
    fun `toString returns prefixed string`() {
        // GIVEN
        val input = "#abc123"

        // WHEN
        val tag = ChallengedPlayerTag.createOrThrow(input)

        // THEN
        assertEquals("#ABC123", tag.toString())
    }

    // endregion

    // region — Consistency checks across companion factories

    @Test
    fun `factory methods createOrNull and createOrThrow are consistent`() {
        // GIVEN
        val validInput = "#abc123"
        val invalidInput = "#ab" // below min length

        // WHEN / THEN
        val successResult = ChallengedPlayerTag.create(validInput)
        val tagOrNull = ChallengedPlayerTag.createOrNull(validInput)
        val tagOrThrow = ChallengedPlayerTag.createOrThrow(validInput)

        assertIs<ChallengedPlayerTag.FactoryResult.Success>(successResult)
        assertNotNull(tagOrNull)
        assertEquals("#ABC123", tagOrNull.stringWithTagPrefix)
        assertEquals("#ABC123", tagOrThrow.stringWithTagPrefix)

        assertNull(ChallengedPlayerTag.createOrNull(invalidInput))
        assertFailsWith<IllegalArgumentException> {
            @Suppress("RETURN_VALUE_NOT_USED")
            ChallengedPlayerTag.createOrThrow(invalidInput)
        }
    }

    // endregion
}
