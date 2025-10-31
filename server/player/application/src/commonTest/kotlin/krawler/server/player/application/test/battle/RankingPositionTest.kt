package krawler.server.player.application.test.battle

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import krawler.server.player.application.battle.RankingPosition
import krawler.server.player.application.battle.isFirst
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RankingPositionTest {

    @Test
    fun `GIVEN a valid ranking position WHEN checking isFirst THEN correct`() {
        // GIVEN
        val first = RankingPosition.FIRST
        val second = RankingPosition.SECOND

        // WHEN & THEN
        assertTrue(first.isFirst())
        assertFalse(second.isFirst())
    }

    @Test
    fun `GIVEN creating a valid ranking position WHEN using create THEN returns success`() {
        // GIVEN
        val value = 10

        // WHEN
        val result = RankingPosition.create(value)

        // THEN
        assertTrue(result.isSuccess)
        assertEquals(result.getOrNull()?.rawInt, value)
    }

    @Test
    fun `GIVEN creating an invalid ranking position WHEN using create THEN returns failure`() {
        // GIVEN
        val value = 0

        // WHEN
        val result = RankingPosition.create(value)

        // THEN
        assertTrue(result.isFailure)
    }

    @Test
    fun `GIVEN creating an invalid ranking position WHEN using createOrThrow THEN throws`() {
        // GIVEN
        val value = 0

        // WHEN & THEN
        assertFailsWith<IllegalArgumentException> {
            val _ = RankingPosition.createOrThrow(value)
        }
    }

    @Test
    fun `GIVEN creating a valid ranking position WHEN using createOrNull THEN returns instance`() {
        // GIVEN
        val value = 5

        // WHEN
        val ranking = RankingPosition.createOrNull(value)

        // THEN
        assertEquals(ranking?.rawInt, value)
    }

    @Test
    fun `GIVEN creating an invalid ranking position WHEN using createOrNull THEN returns null`() {
        // GIVEN
        val value = -3

        // WHEN
        val ranking = RankingPosition.createOrNull(value)

        // THEN
        assertNull(ranking)
    }
}
