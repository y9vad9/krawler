package krawler.user.domain.test

import krawler.user.domain.BrawlStarsPlayerName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LinkedPlayerNameTest {

    // region Success cases

    @Test
    fun `creates LinkedPlayerName with minimal valid length`() {
        // GIVEN
        val input = "a".repeat(BrawlStarsPlayerName.MIN_LENGTH)

        // WHEN
        val result = BrawlStarsPlayerName.create(input)

        // THEN
        assertIs<BrawlStarsPlayerName.FactoryResult.Success>(result)
        assertEquals(input, result.value.string)
    }

    @Test
    fun `creates LinkedPlayerName with maximal valid length`() {
        // GIVEN
        val input = "a".repeat(BrawlStarsPlayerName.MAX_LENGTH)

        // WHEN
        val result = BrawlStarsPlayerName.create(input)

        // THEN
        assertIs<BrawlStarsPlayerName.FactoryResult.Success>(result)
        assertEquals(input, result.value.string)
    }

    @Test
    fun `creates LinkedPlayerName with valid arbitrary input`() {
        // GIVEN
        val input = "sukara"

        // WHEN
        val result = BrawlStarsPlayerName.create(input)

        // THEN
        assertIs<BrawlStarsPlayerName.FactoryResult.Success>(result)
        assertEquals(input, result.value.string)
    }

    @Test
    fun `created LinkedPlayerName stores correct string`() {
        // GIVEN
        val input = "ValidName"

        // WHEN
        val result = BrawlStarsPlayerName.create(input)

        // THEN
        assertIs<BrawlStarsPlayerName.FactoryResult.Success>(result)
        assertEquals(input, result.value.string)
    }

    // endregion

    // region Failure cases

    @Test
    fun `fails for input just below minimum and just above maximum`() {
        // GIVEN
        val tooShort = "a".repeat(BrawlStarsPlayerName.MIN_LENGTH - 1)
        val tooLong = "a".repeat(BrawlStarsPlayerName.MAX_LENGTH + 1)

        // WHEN
        val shortResult = BrawlStarsPlayerName.create(tooShort)
        val longResult = BrawlStarsPlayerName.create(tooLong)

        // THEN
        assertIs<BrawlStarsPlayerName.FactoryResult.NameNotWithinRange>(shortResult)
        assertIs<BrawlStarsPlayerName.FactoryResult.NameNotWithinRange>(longResult)
    }

    @Test
    fun `fails create LinkedPlayerName with invalid arbitrary input`() {
        // GIVEN
        val input = "veryveryveryverylongname"

        // WHEN
        val result = BrawlStarsPlayerName.create(input)

        // THEN
        assertIs<BrawlStarsPlayerName.FactoryResult.NameNotWithinRange>(result)
    }

    // endregion

    // region Companion consistency checks

    @Test
    fun `createOrThrow throws when create fails`() {
        // GIVEN
        val invalidInput = "x".repeat(BrawlStarsPlayerName.MAX_LENGTH + 1)

        // WHEN / THEN
        val exception = assertFailsWith<IllegalArgumentException> {
            @Suppress("RETURN_VALUE_NOT_USED")
            BrawlStarsPlayerName.createOrThrow(invalidInput)
        }

        assertTrue(exception.message?.contains("NameNotWithinRange") ?: true)
    }

    @Test
    fun `createOrNull returns null when create fails`() {
        // GIVEN
        val invalidInput = "x".repeat(BrawlStarsPlayerName.MAX_LENGTH + 1)

        // WHEN
        val result = BrawlStarsPlayerName.createOrNull(invalidInput)

        // THEN
        assertNull(result)
    }

    @Test
    fun `createOrThrow and createOrNull behave like create for success`() {
        // GIVEN
        val validInput = "ValidName"

        // WHEN
        val successResult = BrawlStarsPlayerName.create(validInput)
        val orThrow = BrawlStarsPlayerName.createOrThrow(validInput)
        val orNull = BrawlStarsPlayerName.createOrNull(validInput)

        // THEN
        assertIs<BrawlStarsPlayerName.FactoryResult.Success>(successResult)
        assertEquals(validInput, orThrow.string)
        assertEquals(validInput, orNull?.string)
    }

    // endregion
}
