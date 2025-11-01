package krawler.server.player.application.test.brawler

import krawler.server.player.application.brawler.BrawlerGadgetId
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class BrawlerGadgetIdTest {

    @Test
    fun `valid gadget ID should be created successfully`() {
        // Given a valid gadget ID within the allowed range
        val validId = 23_005_000

        // When constructing a BrawlerGadgetId
        val gadgetId = BrawlerGadgetId(rawInt = validId)

        // Then the rawInt should match the input
        assertEquals(
            expected = validId,
            actual = gadgetId.rawInt,
            message = "Gadget ID's rawInt should equal the input value"
        )
    }

    @Test
    fun `gadget ID below MIN_VALUE should throw exception`() {
        // Given a gadget ID below the minimum allowed
        val tooLowId = BrawlerGadgetId.MIN_VALUE - 1

        // When / Then
        val exception = assertFailsWith<IllegalArgumentException> {
            val _ = BrawlerGadgetId(rawInt = tooLowId)
        }

        assertContains(
            charSequence = exception.message ?: "",
            other = "between ${BrawlerGadgetId.MIN_VALUE} and ${BrawlerGadgetId.MAX_VALUE}",
            message = "Exception message should indicate valid range"
        )
    }

    @Test
    fun `gadget ID above MAX_VALUE should throw exception`() {
        // Given a gadget ID above the maximum allowed
        val tooHighId = BrawlerGadgetId.MAX_VALUE + 1

        // When / Then
        val exception = assertFailsWith<IllegalArgumentException> {
            val _ = BrawlerGadgetId(rawInt = tooHighId)
        }

        assertContains(
            charSequence = exception.message ?: "",
            other = "between ${BrawlerGadgetId.MIN_VALUE} and ${BrawlerGadgetId.MAX_VALUE}",
            message = "Exception message should indicate valid range"
        )
    }

    @Test
    fun `comparison between gadget IDs should work correctly`() {
        // Given two valid gadget IDs
        val lower = BrawlerGadgetId(rawInt = BrawlerGadgetId.MIN_VALUE)
        val higher = BrawlerGadgetId(rawInt = BrawlerGadgetId.MAX_VALUE)

        // Then compareTo returns correct ordering
        assertTrue(
            actual = lower < higher,
            message = "Lower ID should be less than higher ID"
        )

        assertTrue(
            actual = higher > lower,
            message = "Higher ID should be greater than lower ID"
        )

        assertEquals(
            expected = 0,
            actual = lower.compareTo(BrawlerGadgetId(rawInt = BrawlerGadgetId.MIN_VALUE)),
            message = "Comparing equal IDs should return 0"
        )
    }

    @Test
    fun `toString should return rawInt as string`() {
        // Given a valid gadget ID
        val id = 23_004_321
        val gadgetId = BrawlerGadgetId(rawInt = id)

        // Then toString should return the string representation of rawInt
        assertEquals(
            expected = id.toString(),
            actual = gadgetId.toString(),
            message = "toString should return rawInt as string"
        )
    }
}
