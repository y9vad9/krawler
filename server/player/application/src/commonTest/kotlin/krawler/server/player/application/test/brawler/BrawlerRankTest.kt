package krawler.server.player.application.test.brawler

import krawler.server.player.application.brawler.BrawlerRank
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class BrawlerRankTest {

    @Test
    fun `given valid BrawlerRank when created then succeeds`() {
        // Given
        val validRank = 25

        // When
        val rank = BrawlerRank(validRank)

        // Then
        assertEquals(validRank, rank.raw)
        assertEquals(validRank.toString(), rank.toString())
    }

    @Test
    fun `given BrawlerRank below MIN when created then throws`() {
        // Given
        val invalidRank = BrawlerRank.MIN.raw - 1

        // When & Then
        val ex = assertFailsWith<IllegalArgumentException> {
            val _ = BrawlerRank(invalidRank)
        }
        assertTrue(ex.message!!.contains("Invalid brawler rank: $invalidRank"))
    }

    @Test
    fun `given BrawlerRank above MAX when created then throws`() {
        // Given
        val invalidRank = BrawlerRank.MAX.raw + 1

        // When & Then
        val ex = assertFailsWith<IllegalArgumentException> {
            val _ = BrawlerRank(invalidRank)
        }
        assertTrue(ex.message!!.contains("Invalid brawler rank: $invalidRank"))
    }

    @Test
    fun `compareTo should return correct ordering`() {
        val low = BrawlerRank(5)
        val high = BrawlerRank(20)

        assertTrue(low < high)
        assertTrue(high > low)
        assertEquals(0, low.compareTo(BrawlerRank(5)))
    }
}
