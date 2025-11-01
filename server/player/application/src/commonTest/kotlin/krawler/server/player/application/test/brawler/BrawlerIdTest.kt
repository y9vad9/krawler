package krawler.server.player.application.test.brawler

import krawler.server.player.application.brawler.BrawlerId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BrawlerIdTest {

    @Test
    fun `valid BrawlerId creation returns Success`() {
        // Given
        val validId = 16_000_100

        // When
        val result = BrawlerId.create(validId)

        // Then
        assertTrue(result is BrawlerId.FactoryResult.Success)
        assertEquals(validId, result.value.rawInt)
    }

    @Test
    fun `BrawlerId below MIN_VALUE returns TooLow`() {
        // Given
        val invalidId = BrawlerId.MIN_VALUE - 1

        // When
        val result = BrawlerId.create(invalidId)

        // Then
        assertEquals(BrawlerId.FactoryResult.TooLow, result)
    }

    @Test
    fun `BrawlerId above MAX_VALUE returns TooHigh`() {
        // Given
        val invalidId = BrawlerId.MAX_VALUE + 1

        // When
        val result = BrawlerId.create(invalidId)

        // Then
        assertEquals(BrawlerId.FactoryResult.TooHigh, result)
    }

    @Test
    fun `createOrThrow returns instance for valid input`() {
        // Given
        val validId = 16_000_200

        // When
        val instance = BrawlerId.createOrThrow(validId)

        // Then
        assertEquals(validId, instance.rawInt)
    }

    @Test
    fun `createOrThrow throws for TooLow`() {
        // Given
        val invalidId = BrawlerId.MIN_VALUE - 5

        // When / Then
        val ex = assertFailsWith<IllegalArgumentException> {
            val _ = BrawlerId.createOrThrow(invalidId)
        }
        assertTrue(ex.message!!.contains("below range"))
    }

    @Test
    fun `createOrThrow throws for TooHigh`() {
        // Given
        val invalidId = BrawlerId.MAX_VALUE + 5

        // When / Then
        val ex = assertFailsWith<IllegalArgumentException> {
            val _ = BrawlerId.createOrThrow(invalidId)
        }
        assertTrue(ex.message!!.contains("above range"))
    }

    @Test
    fun `createOrNull returns instance for valid input`() {
        // Given
        val validId = 16_000_300

        // When
        val instance = BrawlerId.createOrNull(validId)

        // Then
        assertNotNull(instance)
        assertEquals(validId, instance.rawInt)
    }

    @Test
    fun `createOrNull returns null for invalid input`() {
        // Given
        val tooLow = BrawlerId.MIN_VALUE - 1
        val tooHigh = BrawlerId.MAX_VALUE + 1

        // When / Then
        assertNull(BrawlerId.createOrNull(tooLow))
        assertNull(BrawlerId.createOrNull(tooHigh))
    }

    @Test
    fun `compareTo works correctly`() {
        // Given
        val id1 = BrawlerId.SHELLY
        val id2 = BrawlerId.COLT

        // Then
        assertTrue(id1 < id2)
        assertTrue(id2 > id1)
        assertEquals(0, id1.compareTo(BrawlerId.SHELLY))
    }
}
