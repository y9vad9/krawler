package krawler.server.player.application.test.battle

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import krawler.server.player.application.battle.RankedMatchmakingType
import krawler.server.player.application.battle.isDuo
import krawler.server.player.application.battle.isSolo
import krawler.server.player.application.battle.isTrio

class RankedMatchmakingTypeTest {

    @Test
    fun `GIVEN SOLO matchmaking type WHEN checking flags THEN correct`() {
        // GIVEN
        val type = RankedMatchmakingType.SOLO

        // WHEN & THEN
        assertTrue(type.isSolo)
        assertFalse(type.isDuo)
        assertFalse(type.isTrio)
    }

    @Test
    fun `GIVEN DUO matchmaking type WHEN checking flags THEN correct`() {
        // GIVEN
        val type = RankedMatchmakingType.DUO

        // WHEN & THEN
        assertTrue(type.isDuo)
        assertFalse(type.isSolo)
        assertFalse(type.isTrio)
    }

    @Test
    fun `GIVEN TRIO matchmaking type WHEN checking flags THEN correct`() {
        // GIVEN
        val type = RankedMatchmakingType.TRIO

        // WHEN & THEN
        assertTrue(type.isTrio)
        assertFalse(type.isSolo)
        assertFalse(type.isDuo)
    }
}
