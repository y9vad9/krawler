package krawler.server.player.application.test.brawler

import krawler.server.player.application.brawler.BrawlerStarPowerId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class BrawlerStarPowerIdTest {

    @Test
    fun `given valid Star Power ID when created then succeeds`() {
        // Given
        val validId = 23_000_500

        // When
        val starPowerId = BrawlerStarPowerId(validId)

        // Then
        assertEquals(validId, starPowerId.rawInt)
        assertEquals(validId.toString(), starPowerId.toString())
    }

    @Test
    fun `given Star Power ID below MIN_VALUE when created then throws`() {
        // Given
        val invalidId = BrawlerStarPowerId.MIN_VALUE - 1

        // When & Then
        val ex = assertFailsWith<IllegalArgumentException> {
            val _ = BrawlerStarPowerId(invalidId)
        }
        assertTrue(ex.message!!.contains("Invalid Star Power ID: $invalidId"))
    }

    @Test
    fun `given Star Power ID above MAX_VALUE when created then throws`() {
        // Given
        val invalidId = BrawlerStarPowerId.MAX_VALUE + 1

        // When & Then
        val ex = assertFailsWith<IllegalArgumentException> {
            val _ = BrawlerStarPowerId(invalidId)
        }
        assertTrue(ex.message!!.contains("Invalid Star Power ID: $invalidId"))
    }

    @Test
    fun `compareTo should return correct ordering`() {
        val low = BrawlerStarPowerId(23_000_001)
        val high = BrawlerStarPowerId(23_000_100)

        assertTrue(low < high)
        assertTrue(high > low)
        assertEquals(0, low.compareTo(BrawlerStarPowerId(23_000_001)))
    }
}
