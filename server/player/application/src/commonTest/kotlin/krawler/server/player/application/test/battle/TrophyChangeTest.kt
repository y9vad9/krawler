package krawler.server.player.application.test.battle

import krawler.server.player.application.battle.TrophyChange
import krawler.server.player.application.battle.isGained
import krawler.server.player.application.battle.isLost
import krawler.server.player.application.battle.isUnchanged
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TrophyChangeTest {

    @Test
    fun `isGained returns true for positive trophy change`() {
        // GIVEN a trophy change that is positive
        val change = TrophyChange(5)

        // THEN isGained should be true
        assertTrue(change.isGained)
        assertFalse(change.isLost)
        assertFalse(change.isUnchanged)
    }

    @Test
    fun `isLost returns true for negative trophy change`() {
        // GIVEN a trophy change that is negative
        val change = TrophyChange(-3)

        // THEN isLost should be true
        assertTrue(change.isLost)
        assertFalse(change.isGained)
        assertFalse(change.isUnchanged)
    }

    @Test
    fun `isUnchanged returns true for zero trophy change`() {
        // GIVEN a trophy change of zero
        val change = TrophyChange(0)

        // THEN isUnchanged should be true
        assertTrue(change.isUnchanged)
        assertFalse(change.isGained)
        assertFalse(change.isLost)
    }

    @Test
    fun `compareTo returns correct ordering`() {
        // GIVEN multiple trophy changes
        val lower = TrophyChange(1)
        val higher = TrophyChange(10)

        // THEN comparison should reflect numeric ordering
        assertTrue(lower < higher)
        assertTrue(higher > lower)
        assertEquals(0, lower.compareTo(TrophyChange(1)))
    }

    @Test
    fun `toString returns rawInt as string`() {
        // GIVEN a trophy change
        val change = TrophyChange(7)

        // THEN toString should return string representation of rawInt
        assertEquals("7", change.toString())
    }
}
