package krawler.server.player.application.test

import krawler.server.player.application.PlayerClubName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class PlayerClubNameTest {

    @Test
    fun `isValid returns true for valid club names`() {
        // GIVEN a list of valid club names
        val validNames = listOf(
            "Alpha", "Bravo123", "Club🚀", "A", "ABCDEFGHIJKLMNO" // 15 chars
        )

        // WHEN we check validity
        validNames.forEach { name ->
            // THEN it should return true
            assertTrue(actual = PlayerClubName.isValid(input = name), message = "Expected $name to be valid")
        }
    }

    @Test
    fun `isValid returns false for invalid club names`() {
        // GIVEN a list of invalid club names
        val invalidNames = listOf(
            "",                       // too short
            "A".repeat(16)            // too long
        )

        // WHEN we check validity
        invalidNames.forEach { name ->
            // THEN it should return false
            assertFalse(actual = PlayerClubName.isValid(input = name), message = "Expected $name to be invalid")
        }
    }

    @Test
    fun `create returns success for valid club name`() {
        // GIVEN a valid club name
        val input = "MyClub"

        // WHEN we create a PlayerClubName
        val result = PlayerClubName.create(input = input)

        // THEN it should succeed and wrap the name
        assertTrue(actual = result.isSuccess)
        assertEquals(expected = "MyClub", actual = result.getOrThrow().rawString)
    }

    @Test
    fun `create returns failure for invalid club name`() {
        // GIVEN an invalid club name
        val input = ""

        // WHEN we attempt to create a PlayerClubName
        val result = PlayerClubName.create(input = input)

        // THEN it should fail
        assertTrue(actual = result.isFailure)
        assertTrue(actual = result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `createOrThrow returns instance for valid club name`() {
        // GIVEN a valid club name
        val input = "CoolClub"

        // WHEN we call createOrThrow
        val clubName = PlayerClubName.createOrThrow(input = input)

        // THEN it should return a PlayerClubName instance
        assertEquals(expected = "CoolClub", actual = clubName.rawString)
    }

    @Test
    fun `createOrThrow throws for invalid club name`() {
        // GIVEN an invalid club name
        val input = ""

        // WHEN/THEN it should throw IllegalArgumentException
        assertFailsWith<IllegalArgumentException> {
            val _ = PlayerClubName.createOrThrow(input = input)
        }
    }

    @Test
    fun `createOrNull returns instance for valid club name`() {
        // GIVEN a valid club name
        val input = "FunClub"

        // WHEN we call createOrNull
        val clubName = PlayerClubName.createOrNull(input = input)

        // THEN it should return a PlayerClubName instance
        assertEquals(expected = "FunClub", actual = clubName?.rawString)
    }

    @Test
    fun `createOrNull returns null for invalid club name`() {
        // GIVEN an invalid club name
        val input = ""

        // WHEN we call createOrNull
        val clubName = PlayerClubName.createOrNull(input = input)

        // THEN it should return null
        assertNull(actual = clubName)
    }

    @Test
    fun `compareTo compares club names lexicographically`() {
        // GIVEN two club names
        val nameA = PlayerClubName.createOrThrow(input = "Alpha")
        val nameB = PlayerClubName.createOrThrow(input = "Beta")

        // THEN compareTo should return negative if first is less
        assertTrue(actual = nameA < nameB)
        assertTrue(actual = nameB > nameA)
        assertEquals(expected = 0, actual = nameA.compareTo(nameA))
    }
}
