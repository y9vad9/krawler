package krawler.server.player.application.test.battle

import krawler.server.player.application.battle.BattleResult
import krawler.server.player.application.battle.isDefeat
import krawler.server.player.application.battle.isDraw
import krawler.server.player.application.battle.isVictory
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BattleResultTest {

    @Test
    fun `victory result should be recognized correctly`() {
        // GIVEN BattleResult.VICTORY
        val result = BattleResult.VICTORY

        // THEN
        assertTrue(result.isVictory)
        assertFalse(result.isDraw)
        assertFalse(result.isDefeat)
    }

    @Test
    fun `draw result should be recognized correctly`() {
        // GIVEN BattleResult.DRAW
        val result = BattleResult.DRAW

        // THEN
        assertFalse(result.isVictory)
        assertTrue(result.isDraw)
        assertFalse(result.isDefeat)
    }

    @Test
    fun `defeat result should be recognized correctly`() {
        // GIVEN BattleResult.DEFEAT
        val result = BattleResult.DEFEAT

        // THEN
        assertFalse(result.isVictory)
        assertFalse(result.isDraw)
        assertTrue(result.isDefeat)
    }
}
