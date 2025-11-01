package krawler.server.player.application.test.brawler

import krawler.server.player.application.brawler.BrawlerGearId
import krawler.server.player.application.brawler.BrawlerGearId.Companion.MAX_VALUE
import krawler.server.player.application.brawler.BrawlerGearId.Companion.MIN_VALUE
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class BrawlerGearIdTest {

    @Test
    fun `valid GearId creation succeeds`() {
        // Given
        val validId = 62_000_000

        // When
        val gearId = BrawlerGearId(validId)

        // Then
        assertEquals(validId, gearId.value)
    }

    @Test
    fun `GearId below MIN_VALUE throws`() {
        // Given
        val tooLow = MIN_VALUE - 1

        // When / Then
        val ex = assertFailsWith<IllegalArgumentException> {
            val _ = BrawlerGearId(tooLow)
        }
        assertTrue(ex.message!!.contains("between $MIN_VALUE and $MAX_VALUE"))
    }

    @Test
    fun `GearId above MAX_VALUE throws`() {
        // Given
        val tooHigh = MAX_VALUE + 1

        // When / Then
        val ex = assertFailsWith<IllegalArgumentException> {
            val _ = BrawlerGearId(tooHigh)
        }
        assertTrue(ex.message!!.contains("between $MIN_VALUE and $MAX_VALUE"))
    }

    @Test
    fun `compareTo works correctly`() {
        // Given
        val gear1 = BrawlerGearId(MIN_VALUE)
        val gear2 = BrawlerGearId(MAX_VALUE)

        // Then
        assertTrue(gear1 < gear2)
        assertTrue(gear2 > gear1)
        assertEquals(0, gear1.compareTo(BrawlerGearId(MIN_VALUE)))
    }
}
