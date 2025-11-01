package krawler.server.player.application.test.battle

import krawler.server.player.application.battle.BattleEventMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class BattleEventModeTest {

    private val modeA = BattleEventMode("brawlBall")
    private val modeB = BattleEventMode("soloShowdown")
    private val modeACopy = BattleEventMode("brawlBall")

    @Test
    fun `compareTo should return zero for identical raw strings`() {
        // GIVEN two BattleEventMode instances with the same rawString
        // WHEN comparing them
        val result = modeA.compareTo(modeACopy)

        // THEN the result is 0
        assertEquals(result, 0)
    }

    @Test
    fun `compareTo should return correct order for different raw strings`() {
        // GIVEN two BattleEventMode instances with different rawStrings
        // WHEN comparing them
        val resultAB = modeA.compareTo(modeB)
        val resultBA = modeB.compareTo(modeA)

        // THEN comparisons reflect lexicographical order
        assertTrue(resultAB < 0)
        assertTrue(resultBA > 0)
    }

    @Test
    fun `equality works correctly`() {
        // GIVEN two BattleEventMode instances
        // WHEN comparing for equality
        // THEN equality behaves as expected
        assertEquals(modeA, modeACopy)
        assertNotEquals(modeA, modeB)
    }
}

