package com.y9vad9.krawler.user.domain.test.value

import com.y9vad9.krawler.user.domain.value.BrawlStarsPlayerName
import com.y9vad9.valdi.ValidationResult
import com.y9vad9.valdi.getOrNull
import com.y9vad9.valdi.isSuccess
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class LinkedPlayerNameTest {

    // Test with valid lengths using companion range boundaries and random lengths within range
    @Test
    fun `creates LinkedPlayerName with minimal valid length`() {
        // GIVEN
        val input = "a".repeat(BrawlStarsPlayerName.MIN_LENGTH)

        // WHEN
        val result = BrawlStarsPlayerName.factory.create(input)

        // THEN
        assertTrue(
            actual = result.isSuccess(),
            message = "Expected success for minimal length input: $input"
        )
        assertEquals(
            expected = input,
            actual = result.getOrNull()?.string,
            message = "Stored string should match input"
        )
    }

    @Test
    fun `creates LinkedPlayerName with maximal valid length`() {
        // GIVEN
        val input = "a".repeat(BrawlStarsPlayerName.MAX_LENGTH)

        // WHEN
        val result = BrawlStarsPlayerName.factory.create(input)

        // THEN
        assertTrue(
            actual = result.isSuccess(),
            message = "Expected success for maximal length input: $input",
        )
        assertEquals(
            expected = input,
            actual = result.getOrNull()?.string,
            message = "Stored string should match input",
        )
    }

    @Test
    fun `fails for input just below minimum and just above maximum`() {
        // GIVEN
        val tooShort = "a".repeat(BrawlStarsPlayerName.MIN_LENGTH - 1)
        val tooLong = "a".repeat(BrawlStarsPlayerName.MAX_LENGTH + 1)

        // WHEN
        val shortResult = BrawlStarsPlayerName.factory.create(tooShort)
        val longResult = BrawlStarsPlayerName.factory.create(tooLong)

        // THEN
        assertIs<ValidationResult.Failure<*>>(
            value = shortResult,
            message = "Short input just below the minimum has not failed.",
        )
        assertIs<ValidationResult.Failure<*>>(
            value = longResult,
            message = "Long input just above the maximum has not failed.",
        )

        assertIs<BrawlStarsPlayerName.NameNotWithinRangeFailure>(shortResult.error)
        assertIs<BrawlStarsPlayerName.NameNotWithinRangeFailure>(longResult.error)
    }

    @Test
    fun `creates LinkedPlayerName with valid arbitrary input`() {
        // GIVEN
        val value = "sukara"

        // WHEN
        val result = BrawlStarsPlayerName.factory.create(value)

        // THEN
        assertIs<ValidationResult.Success<*>>(
            value = result,
            message = "Short input just below the minimum has not failed.",
        )
    }

    @Test
    fun `fails create LinkedPlayerName with invalid arbitrary input`() {
        // GIVEN
        val value = "veryveryveryverylongname"

        // WHEN
        val result = BrawlStarsPlayerName.factory.create(value)

        // THEN
        assertIs<ValidationResult.Failure<*>>(result)
        assertIs<BrawlStarsPlayerName.NameNotWithinRangeFailure>(result.error)
    }

    // Test that constructed object holds the correct string (sanity check)
    @Test
    fun `created LinkedPlayerName stores correct string`() {
        // GIVEN
        val input = "ValidName"

        // WHEN
        val result = BrawlStarsPlayerName.factory.create(input)

        // THEN
        assertTrue(result.isSuccess())
        val name = result.getOrNull()
        assertNotNull(name)
        assertEquals(
            expected = input,
            actual = name.string,
            message = "Stored string must equal the input"
        )
    }
}
