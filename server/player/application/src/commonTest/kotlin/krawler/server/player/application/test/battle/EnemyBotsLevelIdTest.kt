package krawler.server.player.application.test.battle

import krawler.server.player.application.battle.EnemyBotsLevelId
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class EnemyBotsLevelIdTest {

    @Test
    fun `valid level should be created successfully`() {
        // Given a valid difficulty level within range
        val validLevel = 3

        // When constructing an EnemyBotsLevelId
        val levelId = EnemyBotsLevelId(rawInt = validLevel)

        // Then the rawInt should match the input
        assertEquals(
            expected = validLevel,
            actual = levelId.rawInt,
            message = "EnemyBotsLevelId's rawInt should equal the input value"
        )
    }

    @Test
    fun `level below MIN_VALUE should throw exception`() {
        // Given a level below the minimum allowed
        val tooLow = EnemyBotsLevelId.MIN_VALUE - 1

        // When / Then
        val exception = assertFailsWith<IllegalArgumentException> {
            val _ = EnemyBotsLevelId(rawInt = tooLow)
        }

        assertContains(
            charSequence = exception.message ?: "",
            other = "${EnemyBotsLevelId.MIN_VALUE}..${EnemyBotsLevelId.MAX_VALUE}",
            message = "Exception message should indicate valid range"
        )
    }

    @Test
    fun `level above MAX_VALUE should throw exception`() {
        // Given a level above the maximum allowed
        val tooHigh = EnemyBotsLevelId.MAX_VALUE + 1

        // When / Then
        val exception = assertFailsWith<IllegalArgumentException> {
            val _ = EnemyBotsLevelId(rawInt = tooHigh)
        }

        assertContains(
            charSequence = exception.message ?: "",
            other = "${EnemyBotsLevelId.MIN_VALUE}..${EnemyBotsLevelId.MAX_VALUE}",
            message = "Exception message should indicate valid range"
        )
    }

    @Test
    fun `comparison between levels should work correctly`() {
        // Given two valid levels
        val lower = EnemyBotsLevelId(1)
        val higher = EnemyBotsLevelId(7)

        // Then compareTo returns correct ordering
        assertTrue(
            actual = lower < higher,
            message = "Lower level should be less than higher level"
        )

        assertTrue(
            actual = higher > lower,
            message = "Higher level should be greater than lower level"
        )

        assertEquals(
            expected = 0,
            actual = lower.compareTo(EnemyBotsLevelId(1)),
            message = "Comparing equal levels should return 0"
        )
    }
}
