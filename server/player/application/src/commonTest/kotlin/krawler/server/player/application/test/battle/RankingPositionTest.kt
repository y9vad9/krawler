package krawler.server.player.application.test.battle

import krawler.server.player.application.battle.RankingPosition
import krawler.server.player.application.battle.isFirst
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RankingPositionTest {

    @Test
    fun `given valid ranking position when created then should hold value`() {
        // Given
        val validValue = 5

        // When
        val ranking = RankingPosition(validValue)

        // Then
        assertEquals(validValue, ranking.rawInt, "RankingPosition should store the given value")
    }

    @Test
    fun `given ranking position 1 when compared to higher then should be less`() {
        // Given
        val first = RankingPosition(1)
        val fifth = RankingPosition(5)

        // When
        val result = first < fifth

        // Then
        assertEquals(true, result, "RankingPosition(1) should be less than RankingPosition(5)")
    }

    @Test
    fun `given invalid ranking position when created then should throw`() {
        // Given
        val invalidValue = 0

        // When & Then
        val exception = assertFailsWith<IllegalArgumentException> {
            val _ = RankingPosition(invalidValue)
        }

        assertEquals("Ranking position must be >= 1, got 0", exception.message)
    }

    @Test
    fun `GIVEN a valid ranking position WHEN checking isFirst THEN correct`() {
        // Given
        val first = RankingPosition(1)
        val second = RankingPosition(2)

        // When & Then
        assertTrue(first.isFirst())
        assertFalse(second.isFirst())
    }
}
