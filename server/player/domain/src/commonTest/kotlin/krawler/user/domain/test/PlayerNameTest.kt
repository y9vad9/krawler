package krawler.user.domain.test

import krawler.user.domain.PlayerName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PlayerNameTest {

    // region Success cases

    @Test
    fun `creates LinkedPlayerName with minimal valid length`() {
        // GIVEN
        val input = "a".repeat(PlayerName.MIN_LENGTH)

        // WHEN
        val result = PlayerName.create(input)

        // THEN
        assertIs<PlayerName.FactoryResult.Success>(result)
        assertEquals(input, result.value.string)
    }

    @Test
    fun `creates LinkedPlayerName with maximal valid length`() {
        // GIVEN
        val input = "a".repeat(PlayerName.MAX_LENGTH)

        // WHEN
        val result = PlayerName.create(input)

        // THEN
        assertIs<PlayerName.FactoryResult.Success>(result)
        assertEquals(input, result.value.string)
    }

    @Test
    fun `creates LinkedPlayerName with valid arbitrary input`() {
        // GIVEN
        val input = "sukara"

        // WHEN
        val result = PlayerName.create(input)

        // THEN
        assertIs<PlayerName.FactoryResult.Success>(result)
        assertEquals(input, result.value.string)
    }

    @Test
    fun `created LinkedPlayerName stores correct string`() {
        // GIVEN
        val input = "ValidName"

        // WHEN
        val result = PlayerName.create(input)

        // THEN
        assertIs<PlayerName.FactoryResult.Success>(result)
        assertEquals(input, result.value.string)
    }

    // endregion

    // region Failure cases

    @Test
    fun `fails for input just below minimum and just above maximum`() {
        // GIVEN
        val tooShort = "a".repeat(PlayerName.MIN_LENGTH - 1)
        val tooLong = "a".repeat(PlayerName.MAX_LENGTH + 1)

        // WHEN
        val shortResult = PlayerName.create(tooShort)
        val longResult = PlayerName.create(tooLong)

        // THEN
        assertIs<PlayerName.FactoryResult.NameNotWithinRange>(shortResult)
        assertIs<PlayerName.FactoryResult.NameNotWithinRange>(longResult)
    }

    @Test
    fun `fails create LinkedPlayerName with invalid arbitrary input`() {
        // GIVEN
        val input = "veryveryveryverylongname"

        // WHEN
        val result = PlayerName.create(input)

        // THEN
        assertIs<PlayerName.FactoryResult.NameNotWithinRange>(result)
    }

    // endregion

    // region Companion consistency checks

    @Test
    fun `createOrThrow throws when create fails`() {
        // GIVEN
        val invalidInput = "x".repeat(PlayerName.MAX_LENGTH + 1)

        // WHEN / THEN
        val exception = assertFailsWith<IllegalArgumentException> {
            @Suppress("RETURN_VALUE_NOT_USED")
            PlayerName.createOrThrow(invalidInput)
        }

        assertTrue(exception.message?.contains("NameNotWithinRange") ?: true)
    }

    @Test
    fun `createOrNull returns null when create fails`() {
        // GIVEN
        val invalidInput = "x".repeat(PlayerName.MAX_LENGTH + 1)

        // WHEN
        val result = PlayerName.createOrNull(invalidInput)

        // THEN
        assertNull(result)
    }

    @Test
    fun `createOrThrow and createOrNull behave like create for success`() {
        // GIVEN
        val validInput = "ValidName"

        // WHEN
        val successResult = PlayerName.create(validInput)
        val orThrow = PlayerName.createOrThrow(validInput)
        val orNull = PlayerName.createOrNull(validInput)

        // THEN
        assertIs<PlayerName.FactoryResult.Success>(successResult)
        assertEquals(validInput, orThrow.string)
        assertEquals(validInput, orNull?.string)
    }

    // endregion
}
