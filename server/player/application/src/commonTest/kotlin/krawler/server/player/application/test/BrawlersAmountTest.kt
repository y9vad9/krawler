package krawler.server.player.application.test

import krawler.server.player.application.BrawlersAmount
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BrawlersAmountTest {

    @Test
    fun `given valid non-negative value when created then should hold the value`() {
        // Given
        val value = 15

        // When
        val amount = BrawlersAmount(value)

        // Then
        assertEquals(value, amount.int, "BrawlersAmount should store the provided non-negative value")
    }

    @Test
    fun `given negative value when created then should throw IllegalArgumentException`() {
        // Given
        val invalidValue = -3

        // When & Then
        val exception = assertFailsWith<IllegalArgumentException> {
            val _ = BrawlersAmount(invalidValue)
        }
        assertEquals("Brawlers amount must be zero or greater, got -3.", exception.message)
    }

    @Test
    fun `given two valid amounts when compared then should reflect correct ordering`() {
        // Given
        val smaller = BrawlersAmount(5)
        val larger = BrawlersAmount(10)

        // When
        val result = smaller < larger

        // Then
        assertEquals(true, result, "Smaller BrawlersAmount should compare less than larger one")
    }

    @Test
    fun `given two valid amounts when subtracted then should produce correct difference`() {
        // Given
        val first = BrawlersAmount(10)
        val second = BrawlersAmount(4)

        // When
        val result = first - second

        // Then
        assertEquals(BrawlersAmount(6), result, "Subtraction should yield the correct non-negative result")
    }

    @Test
    fun `given subtraction resulting in negative value when performed then should throw`() {
        // Given
        val first = BrawlersAmount(2)
        val second = BrawlersAmount(5)

        // When & Then
        val exception = assertFailsWith<IllegalArgumentException> {
            val _ = first - second
        }
        assertEquals("Brawlers amount must be zero or greater, got -3.", exception.message)
    }

    @Test
    fun `given amount when converted to string then should return its integer representation`() {
        // Given
        val amount = BrawlersAmount(12)

        // When
        val stringValue = amount.toString()

        // Then
        assertEquals("12", stringValue, "toString should return the numeric string representation")
    }
}
