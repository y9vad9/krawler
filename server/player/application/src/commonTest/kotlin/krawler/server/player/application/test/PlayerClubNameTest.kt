package krawler.server.player.application.test

import krawler.server.player.application.PlayerClubName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PlayerClubNameTest {

    @Test
    fun `given valid club name within limits when created then should hold the value`() {
        // Given
        val validName = "Legends"

        // When
        val clubName = PlayerClubName(validName)

        // Then
        assertEquals(validName, clubName.rawString, "PlayerClubName should store the valid name string")
    }

    @Test
    fun `given club name shorter than minimum when created then should throw IllegalArgumentException`() {
        // Given
        val invalidName = ""

        // When & Then
        val exception = assertFailsWith<IllegalArgumentException> {
            val _ = PlayerClubName(invalidName)
        }
        assertEquals(
            "Club name length must be between ${PlayerClubName.MIN_LENGTH} and ${PlayerClubName.MAX_LENGTH}" +
                    " characters: ",
            exception.message
        )
    }

    @Test
    fun `given club name longer than maximum when created then should throw IllegalArgumentException`() {
        // Given
        val invalidName = "X".repeat(PlayerClubName.MAX_LENGTH + 1)

        // When & Then
        val exception = assertFailsWith<IllegalArgumentException> {
            val _ = PlayerClubName(invalidName)
        }
        assertEquals(
            "Club name length must be between ${PlayerClubName.MIN_LENGTH} and ${PlayerClubName.MAX_LENGTH}" +
                    " characters: $invalidName",
            exception.message
        )
    }

    @Test
    fun `given two club names when compared then should reflect lexicographical order`() {
        // Given
        val first = PlayerClubName("Alpha")
        val second = PlayerClubName("Bravo")

        // When
        val result = first < second

        // Then
        assertEquals(true, result, "Comparison should follow lexicographical order")
    }

    @Test
    fun `given club name when converted to string then should return its raw value`() {
        // Given
        val name = "Elite Squad"
        val clubName = PlayerClubName(name)

        // When
        val stringValue = clubName.toString()

        // Then
        assertEquals(name, stringValue, "toString should return the raw club name string")
    }

    @Test
    fun `given club name with emojis and symbols when created then should be valid`() {
        // Given
        val nameWithEmojis = "🔥🔥 Legends ✨"

        // When
        val clubName = PlayerClubName(nameWithEmojis)

        // Then
        assertEquals(nameWithEmojis, clubName.rawString, "Club name should support emojis and symbols")
    }
}
