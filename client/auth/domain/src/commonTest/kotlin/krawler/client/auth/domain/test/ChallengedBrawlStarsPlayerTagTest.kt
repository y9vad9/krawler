package krawler.client.auth.domain.test

import krawler.client.auth.domain.ChallengedBrawlStarsPlayerTag
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ChallengedBrawlStarsPlayerTagTest {

    // region — Core factory tests

    @Test
    fun `creates tag with valid input without prefix`() {
        // GIVEN
        val input = "abc123"

        // WHEN
        val result = ChallengedBrawlStarsPlayerTag.create(input)

        // THEN
        assertIs<ChallengedBrawlStarsPlayerTag.FactoryResult.Success>(
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
        val result = ChallengedBrawlStarsPlayerTag.create(input)

        // THEN
        assertIs<ChallengedBrawlStarsPlayerTag.FactoryResult.Success>(result)
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
        val result = ChallengedBrawlStarsPlayerTag.create(input)

        // THEN
        assertIs<ChallengedBrawlStarsPlayerTag.FactoryResult.TagNotWithinRange>(result)
    }

    @Test
    fun `fails on input above maximum length`() {
        // GIVEN
        val input = "#${"A".repeat(15)}"

        // WHEN
        val result = ChallengedBrawlStarsPlayerTag.create(input)

        // THEN
        assertIs<ChallengedBrawlStarsPlayerTag.FactoryResult.TagNotWithinRange>(result)
    }

    @Test
    fun `fails on invalid format with special characters`() {
        // GIVEN
        val input = "#ABC@1"

        // WHEN
        val result = ChallengedBrawlStarsPlayerTag.create(input)

        // THEN
        assertIs<ChallengedBrawlStarsPlayerTag.FactoryResult.InvalidFormat>(result)
    }

    @Test
    fun `fails on whitespace-only input`() {
        // GIVEN
        val input = "   "

        // WHEN
        val result = ChallengedBrawlStarsPlayerTag.create(input)

        // THEN
        assertIs<ChallengedBrawlStarsPlayerTag.FactoryResult.InvalidFormat>(result)
    }

    @Test
    fun `stores and normalizes correctly with mixed case and no prefix`() {
        // GIVEN
        val input = "aBcDe1"

        // WHEN
        val result = ChallengedBrawlStarsPlayerTag.create(input)

        // THEN
        assertIs<ChallengedBrawlStarsPlayerTag.FactoryResult.Success>(result)
        val tag = result.value
        assertEquals("#ABCDE1", tag.stringWithTagPrefix)
        assertEquals("ABCDE1", tag.stringWithoutTagPrefix)
    }

    @Test
    fun `toString returns prefixed string`() {
        // GIVEN
        val input = "#abc123"

        // WHEN
        val tag = ChallengedBrawlStarsPlayerTag.createOrThrow(input)

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
        val successResult = ChallengedBrawlStarsPlayerTag.create(validInput)
        val tagOrNull = ChallengedBrawlStarsPlayerTag.createOrNull(validInput)
        val tagOrThrow = ChallengedBrawlStarsPlayerTag.createOrThrow(validInput)

        assertIs<ChallengedBrawlStarsPlayerTag.FactoryResult.Success>(successResult)
        assertNotNull(tagOrNull)
        assertEquals("#ABC123", tagOrNull.stringWithTagPrefix)
        assertEquals("#ABC123", tagOrThrow.stringWithTagPrefix)

        assertNull(ChallengedBrawlStarsPlayerTag.createOrNull(invalidInput))
        assertFailsWith<IllegalArgumentException> {
            @Suppress("RETURN_VALUE_NOT_USED")
            ChallengedBrawlStarsPlayerTag.createOrThrow(invalidInput)
        }
    }

    // endregion
}
