package krawler.server.player.application.test.brawler

import krawler.server.player.application.brawler.BrawlerPowerLevel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class BrawlerPowerLevelTest {

    @Test
    fun `given valid BrawlerPowerLevel when created then succeeds`() {
        // Given
        val validLevel = 5

        // When
        val level = BrawlerPowerLevel(validLevel)

        // Then
        assertEquals(validLevel, level.int)
        assertEquals(validLevel.toString(), level.toString())
    }

    @Test
    fun `given BrawlerPowerLevel below MIN when created then throws`() {
        // Given
        val invalidLevel = BrawlerPowerLevel.MIN_VALUE - 1

        // When & Then
        val ex = assertFailsWith<IllegalArgumentException> {
            val _ = BrawlerPowerLevel(invalidLevel)
        }
        assertTrue(ex.message!!.contains("BrawlerPowerLevel must be between"))
    }

    @Test
    fun `given BrawlerPowerLevel above MAX when created then throws`() {
        // Given
        val invalidLevel = BrawlerPowerLevel.MAX_VALUE + 1

        // When & Then
        val ex = assertFailsWith<IllegalArgumentException> {
            val _ = BrawlerPowerLevel(invalidLevel)
        }
        assertTrue(ex.message!!.contains("BrawlerPowerLevel must be between"))
    }

    @Test
    fun `compareTo should return correct ordering`() {
        val low = BrawlerPowerLevel(3)
        val high = BrawlerPowerLevel(8)

        assertTrue(low < high)
        assertTrue(high > low)
        assertEquals(0, low.compareTo(BrawlerPowerLevel(3)))
    }

    @Test
    fun `MIN and MAX constants are correct`() {
        assertEquals(BrawlerPowerLevel.MIN_VALUE, BrawlerPowerLevel.MIN.int)
        assertEquals(BrawlerPowerLevel.MAX_VALUE, BrawlerPowerLevel.MAX.int)
    }
}
