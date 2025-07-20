package com.y9vad9.brawlex.user.domain.test.value

import com.y9vad9.brawlex.user.domain.value.UserName
import com.y9vad9.valdi.ValidationResult
import com.y9vad9.valdi.getOrNull
import com.y9vad9.valdi.isSuccess
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class UserNameTest {

    @Test
    fun `creates UserName with minimal valid length`() {
        // GIVEN
        val input = "a".repeat(UserName.MIN_LENGTH)

        // WHEN
        val result = UserName.factory.create(input)

        // THEN
        assertTrue(
            actual = result.isSuccess(),
            message = "Expected success for minimal valid input: $input",
        )
        assertEquals(
            expected = input,
            actual = result.getOrNull()?.string,
            message = "Stored string must match input"
        )
    }

    @Test
    fun `creates UserName with maximal valid length`() {
        // GIVEN
        val input = "a".repeat(UserName.MAX_LENGTH)

        // WHEN
        val result = UserName.factory.create(input)

        // THEN
        assertTrue(
            actual = result.isSuccess(),
            message = "Expected success for maximal valid input: $input",
        )
        assertEquals(
            expected = input,
            actual = result.getOrNull()?.string,
            message = "Stored string must match input",
        )
    }

    @Test
    fun `fails for input just below minimum and just above maximum`() {
        // GIVEN
        val tooShort = "a".repeat(UserName.MIN_LENGTH - 1)
        val tooLong = "a".repeat(UserName.MAX_LENGTH + 1)

        // WHEN
        val shortResult = UserName.factory.create(tooShort)
        val longResult = UserName.factory.create(tooLong)

        // THEN
        assertIs<ValidationResult.Failure<*>>(
            value = shortResult,
            message = "Expected failure for input below minimum",
        )
        assertIs<ValidationResult.Failure<*>>(
            value = longResult,
            message = "Expected failure for input above maximum",
        )

        assertIs<UserName.NameNotWithinRangeFailure>(shortResult.error)
        assertIs<UserName.NameNotWithinRangeFailure>(longResult.error)
    }

    @Test
    fun `creates UserName with valid arbitrary input`() {
        // GIVEN
        val input = "sakura"

        // WHEN
        val result = UserName.factory.create(input)

        // THEN
        assertTrue(
            actual = result.isSuccess(),
            message = "Expected success for valid input: $input"
        )
        assertEquals(
            expected = input,
            actual = result.getOrNull()?.string,
            message = "Stored string must match input",
        )
    }

    @Test
    fun `fails create UserName with invalid arbitrary input`() {
        // GIVEN
        val input = "a".repeat(500)

        // WHEN
        val result = UserName.factory.create(input)

        // THEN
        assertIs<ValidationResult.Failure<*>>(
            value = result,
            message = "Expected failure for input exceeding max length",
        )
        assertIs<UserName.NameNotWithinRangeFailure>(result.error)
    }

    @Test
    fun `created UserName stores correct string`() {
        // GIVEN
        val input = "ValidUser"

        // WHEN
        val result = UserName.factory.create(input)

        // THEN
        assertTrue(
            actual = result.isSuccess(),
            message = "Expected success for input: $input",
        )
        val name = result.getOrNull()
        assertNotNull(
            actual = name,
            message = "Expected non-null result for valid input",
        )
        assertEquals(
            expected = input,
            actual = name.string,
            message = "Stored string must match input",
        )
    }
}
