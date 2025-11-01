package krawler.server.player.application.test

import krawler.server.player.application.PlayerVictories
import krawler.server.player.application.VictoryAmount
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Unit tests for [PlayerVictories].
 */
class PlayerVictoriesTest {

    @Test
    fun `total returns sum of solo duo and trio victories`() {
        // GIVEN
        val solo = VictoryAmount(10)
        val duo = VictoryAmount(20)
        val trio = VictoryAmount(30)
        val victories = PlayerVictories(solo = solo, duo = duo, trio = trio)

        // WHEN
        val total = victories.total

        // THEN
        assertEquals(expected = 60, actual = total.int)
    }
}
