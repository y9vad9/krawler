package krawler.server.player.application.test.battle

import krawler.server.player.application.battle.BattleEventId
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BattleEventIdTest {

    @Test
    fun `valid event ID should be created successfully`() {
        // Given a valid event ID within the known range
        val validId = 15_050_000

        // When creating a BattleEventId
        val eventId = BattleEventId.createOrThrow(int = validId)

        // Then the rawInt should match the input
        assertEquals(
            expected = validId,
            actual = eventId.rawInt,
            message = "BattleEventId's rawInt should equal the input value"
        )
    }

    @Test
    fun `event ID below MIN_VALUE should throw exception`() {
        // Given an event ID below the minimum allowed
        val tooLow = BattleEventId.MIN_VALUE - 1

        // When / Then
        val exception = assertFailsWith<IllegalArgumentException> {
            val _ = BattleEventId.createOrThrow(int = tooLow)
        }

        assertContains(
            charSequence = exception.message ?: "",
            other = "${BattleEventId.MIN_VALUE}..${BattleEventId.MAX_VALUE}",
            message = "Exception message should indicate valid range"
        )
    }

    @Test
    fun `event ID above MAX_VALUE should throw exception`() {
        // Given an event ID above the maximum allowed
        val tooHigh = BattleEventId.MAX_VALUE + 1

        // When / Then
        val exception = assertFailsWith<IllegalArgumentException> {
            val _ = BattleEventId.createOrThrow(int = tooHigh)
        }

        assertContains(
            charSequence = exception.message ?: "",
            other = "${BattleEventId.MIN_VALUE}..${BattleEventId.MAX_VALUE}",
            message = "Exception message should indicate valid range"
        )
    }

    @Test
    fun `createOrNull should return null for invalid IDs`() {
        // Given invalid IDs
        val tooLow = BattleEventId.MIN_VALUE - 10
        val tooHigh = BattleEventId.MAX_VALUE + 10

        // When / Then
        assertNull(
            actual = BattleEventId.createOrNull(int = tooLow),
            message = "createOrNull should return null for ID below minimum"
        )

        assertNull(
            actual = BattleEventId.createOrNull(int = tooHigh),
            message = "createOrNull should return null for ID above maximum"
        )
    }

    @Test
    fun `comparison between IDs should work correctly`() {
        // Given two valid IDs
        val lower = BattleEventId.createOrThrow(int = 15_000_001)
        val higher = BattleEventId.createOrThrow(int = 15_050_000)

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
            actual = lower.compareTo(BattleEventId.createOrThrow(int = 15_000_001)),
            message = "Comparing equal IDs should return 0"
        )
    }
}
