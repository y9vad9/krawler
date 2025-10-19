package krawler.user.domain.test

import krawler.user.domain.LinkedTelegramUserName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LinkedTelegramUserNameTest {

    // region — Successful creation tests

    @Test
    fun `creates UserName with minimal valid length`() {
        // GIVEN
        val input = "a".repeat(LinkedTelegramUserName.MIN_LENGTH)

        // WHEN
        val result = LinkedTelegramUserName.create(input)

        // THEN
        assertIs<LinkedTelegramUserName.FactoryResult.Success>(result)
        assertEquals(
            expected = input,
            actual = result.value.string,
            message = "Stored string must match input",
        )
    }

    @Test
    fun `creates UserName with maximal valid length`() {
        // GIVEN
        val input = "a".repeat(LinkedTelegramUserName.MAX_LENGTH)

        // WHEN
        val result = LinkedTelegramUserName.create(input)

        // THEN
        assertIs<LinkedTelegramUserName.FactoryResult.Success>(result)
        assertEquals(
            expected = input,
            actual = result.value.string,
            message = "Stored string must match input",
        )
    }

    @Test
    fun `creates UserName with valid arbitrary input`() {
        // GIVEN
        val input = "sukara"

        // WHEN
        val result = LinkedTelegramUserName.create(input)

        // THEN
        assertIs<LinkedTelegramUserName.FactoryResult.Success>(result)
        assertEquals(
            expected = input,
            actual = result.value.string,
            message = "Stored string must match input",
        )
    }

    @Test
    fun `created UserName stores correct string`() {
        // GIVEN
        val input = "ValidUser"

        // WHEN
        val result = LinkedTelegramUserName.create(input)

        // THEN
        assertIs<LinkedTelegramUserName.FactoryResult.Success>(result)
        assertEquals(input, result.value.string)
    }

    // endregion

    // region — Failure creation tests

    @Test
    fun `fails for input just below minimum and just above maximum`() {
        // GIVEN
        val tooShort = "a".repeat(LinkedTelegramUserName.MIN_LENGTH - 1)
        val tooLong = "a".repeat(LinkedTelegramUserName.MAX_LENGTH + 1)

        // WHEN
        val shortResult = LinkedTelegramUserName.create(tooShort)
        val longResult = LinkedTelegramUserName.create(tooLong)

        // THEN
        assertIs<LinkedTelegramUserName.FactoryResult.NameNotWithinRange>(shortResult)
        assertIs<LinkedTelegramUserName.FactoryResult.NameNotWithinRange>(longResult)
    }

    @Test
    fun `fails create UserName with invalid arbitrary input`() {
        // GIVEN
        val input = "a".repeat(500)

        // WHEN
        val result = LinkedTelegramUserName.create(input)

        // THEN
        assertIs<LinkedTelegramUserName.FactoryResult.NameNotWithinRange>(result)
    }

    // endregion

    // region — Consistency tests for createOrNull and createOrThrow

    @Test
    fun `createOrNull returns name for valid input`() {
        // GIVEN
        val input = "ValidName"

        // WHEN
        val name = LinkedTelegramUserName.createOrNull(input)

        // THEN
        assertNotNull(name)
        assertEquals(input, name.string)
    }

    @Test
    fun `createOrNull returns null for invalid input`() {
        // GIVEN
        val input = "a".repeat(0)

        // WHEN
        val name = LinkedTelegramUserName.createOrNull(input)

        // THEN
        assertNull(name)
    }

    @Test
    fun `createOrThrow returns name for valid input`() {
        // GIVEN
        val input = "hello"

        // WHEN
        val name = LinkedTelegramUserName.createOrThrow(input)

        // THEN
        assertEquals("hello", name.string)
    }

    @Test
    fun `createOrThrow throws for invalid input`() {
        // GIVEN
        val input = ""

        // THEN
        val exception = assertFailsWith<IllegalArgumentException> {
            @Suppress("RETURN_VALUE_NOT_USED")
            LinkedTelegramUserName.createOrThrow(input)
        }
        assertTrue(exception.message!!.contains("returned"))
    }

    // endregion
}

