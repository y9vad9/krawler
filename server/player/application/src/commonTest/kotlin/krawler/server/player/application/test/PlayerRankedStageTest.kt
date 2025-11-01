package krawler.server.player.application.test

import krawler.server.player.application.PlayerRankedStage
import krawler.server.player.application.isAtLeastBronzeOne
import krawler.server.player.application.isAtLeastBronzeThree
import krawler.server.player.application.isAtLeastBronzeTwo
import krawler.server.player.application.isAtLeastDiamondOne
import krawler.server.player.application.isAtLeastGoldOne
import krawler.server.player.application.isAtLeastGoldThree
import krawler.server.player.application.isAtLeastGoldTwo
import krawler.server.player.application.isAtLeastLegendaryOne
import krawler.server.player.application.isAtLeastMasterOne
import krawler.server.player.application.isAtLeastMythicOne
import krawler.server.player.application.isAtLeastSilverOne
import krawler.server.player.application.isAtLeastSilverThree
import krawler.server.player.application.isAtLeastSilverTwo
import krawler.server.player.application.isBronze
import krawler.server.player.application.isDiamond
import krawler.server.player.application.isGold
import krawler.server.player.application.isLegendary
import krawler.server.player.application.isMaster
import krawler.server.player.application.isMythic
import krawler.server.player.application.isSilver
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for [PlayerRankedStage] and its extension properties.
 */
class PlayerRankedStageTest {

    @Test
    fun `ordinal comparison works correctly`() {
        // GIVEN
        val bronzeOne = PlayerRankedStage.BRONZE_ONE
        val silverOne = PlayerRankedStage.SILVER_ONE
        val goldThree = PlayerRankedStage.GOLD_THREE

        // THEN
        assertTrue(actual = silverOne > bronzeOne)
        assertTrue(actual = goldThree > silverOne)
        assertTrue(actual = bronzeOne < goldThree)
        assertEquals(expected = 0, actual = bronzeOne.compareTo(PlayerRankedStage.BRONZE_ONE))
    }

    @Test
    fun `isAtLeast returns true when stage is equal or higher`() {
        // GIVEN
        val silverTwo = PlayerRankedStage.SILVER_TWO
        val silverOne = PlayerRankedStage.SILVER_ONE
        val goldOne = PlayerRankedStage.GOLD_ONE

        // THEN
        assertTrue(actual = silverTwo.isAtLeast(silverOne))
        assertTrue(actual = goldOne.isAtLeast(silverTwo))
        assertFalse(actual = silverOne.isAtLeast(goldOne))
    }

    @Test
    fun `tier constants match their expected ordinals`() {
        // Bronze
        assertEquals(expected = 1, actual = PlayerRankedStage.BRONZE_ONE.ordinal)
        assertEquals(expected = 2, actual = PlayerRankedStage.BRONZE_TWO.ordinal)
        assertEquals(expected = 3, actual = PlayerRankedStage.BRONZE_THREE.ordinal)

        // Silver
        assertEquals(expected = 4, actual = PlayerRankedStage.SILVER_ONE.ordinal)
        assertEquals(expected = 5, actual = PlayerRankedStage.SILVER_TWO.ordinal)
        assertEquals(expected = 6, actual = PlayerRankedStage.SILVER_THREE.ordinal)

        // Gold
        assertEquals(expected = 7, actual = PlayerRankedStage.GOLD_ONE.ordinal)
        assertEquals(expected = 8, actual = PlayerRankedStage.GOLD_TWO.ordinal)
        assertEquals(expected = 9, actual = PlayerRankedStage.GOLD_THREE.ordinal)

        // Diamond
        assertEquals(expected = 10, actual = PlayerRankedStage.DIAMOND_ONE.ordinal)
        assertEquals(expected = 11, actual = PlayerRankedStage.DIAMOND_TWO.ordinal)
        assertEquals(expected = 12, actual = PlayerRankedStage.DIAMOND_THREE.ordinal)

        // Mythic
        assertEquals(expected = 13, actual = PlayerRankedStage.MYTHIC_ONE.ordinal)
        assertEquals(expected = 14, actual = PlayerRankedStage.MYTHIC_TWO.ordinal)
        assertEquals(expected = 15, actual = PlayerRankedStage.MYTHIC_THREE.ordinal)

        // Legendary
        assertEquals(expected = 16, actual = PlayerRankedStage.LEGENDARY_ONE.ordinal)
        assertEquals(expected = 17, actual = PlayerRankedStage.LEGENDARY_TWO.ordinal)
        assertEquals(expected = 18, actual = PlayerRankedStage.LEGENDARY_THREE.ordinal)

        // Master
        assertEquals(expected = 19, actual = PlayerRankedStage.MASTER_ONE.ordinal)
        assertEquals(expected = 20, actual = PlayerRankedStage.MASTER_TWO.ordinal)
        assertEquals(expected = 21, actual = PlayerRankedStage.MASTER_THREE.ordinal)
    }

    @Test
    fun `tier extension properties return correct booleans`() {
        // GIVEN
        val bronzeTwo = PlayerRankedStage.BRONZE_TWO
        val silverThree = PlayerRankedStage.SILVER_THREE
        val goldOne = PlayerRankedStage.GOLD_ONE
        val diamondTwo = PlayerRankedStage.DIAMOND_TWO
        val mythicThree = PlayerRankedStage.MYTHIC_THREE
        val legendaryOne = PlayerRankedStage.LEGENDARY_ONE
        val masterThree = PlayerRankedStage.MASTER_THREE

        // THEN
        // Bronze
        assertTrue(actual = bronzeTwo.isBronze)
        assertFalse(actual = bronzeTwo.isSilver)
        assertFalse(actual = bronzeTwo.isGold)

        // Silver
        assertTrue(actual = silverThree.isSilver)
        assertFalse(actual = silverThree.isGold)

        // Gold
        assertTrue(actual = goldOne.isGold)
        assertFalse(actual = goldOne.isDiamond)

        // Diamond
        assertTrue(actual = diamondTwo.isDiamond)
        assertFalse(actual = diamondTwo.isMythic)

        // Mythic
        assertTrue(actual = mythicThree.isMythic)
        assertFalse(actual = mythicThree.isLegendary)

        // Legendary
        assertTrue(actual = legendaryOne.isLegendary)
        assertFalse(actual = legendaryOne.isMaster)

        // Master
        assertTrue(actual = masterThree.isMaster)
        assertFalse(actual = masterThree.isBronze)
    }

    @Test
    fun `isAtLeast tier helpers work correctly`() {
        // GIVEN
        val goldTwo = PlayerRankedStage.GOLD_TWO

        // THEN
        assertTrue(actual = goldTwo.isAtLeastBronzeOne)
        assertTrue(actual = goldTwo.isAtLeastSilverOne)
        assertTrue(actual = goldTwo.isAtLeastGoldOne)
        assertFalse(actual = goldTwo.isAtLeastGoldThree)
        assertFalse(actual = goldTwo.isAtLeastDiamondOne)
    }

    @Test
    fun `toString returns human-readable stage names`() {
        // GIVEN
        val stages = listOf(
            PlayerRankedStage.BRONZE_ONE to "RankedStage.BRONZE_ONE",
            PlayerRankedStage.SILVER_TWO to "RankedStage.SILVER_TWO",
            PlayerRankedStage.GOLD_THREE to "RankedStage.GOLD_THREE",
            PlayerRankedStage.DIAMOND_ONE to "RankedStage.DIAMOND_ONE",
            PlayerRankedStage.MYTHIC_TWO to "RankedStage.MYTHIC_TWO",
            PlayerRankedStage.LEGENDARY_THREE to "RankedStage.LEGENDARY_THREE",
            PlayerRankedStage.MASTER_ONE to "RankedStage.MASTER_ONE",
            PlayerRankedStage(99) to "RankedStage.UNKNOWN(99)"
        )

        // THEN
        stages.forEach { (stage, expectedName) ->
            assertEquals(expected = expectedName, actual = stage.toString())
        }
    }

    @Test
    fun `distinct stages have distinct ordinals`() {
        // GIVEN
        val stages = listOf(
            PlayerRankedStage.BRONZE_ONE,
            PlayerRankedStage.SILVER_ONE,
            PlayerRankedStage.GOLD_ONE,
            PlayerRankedStage.DIAMOND_ONE,
            PlayerRankedStage.MYTHIC_ONE,
            PlayerRankedStage.LEGENDARY_ONE,
            PlayerRankedStage.MASTER_ONE
        )

        // THEN
        val ordinals = stages.map { it.ordinal }
        assertEquals(expected = ordinals.distinct().size, actual = stages.size)
    }

    @Test
    fun `all isAtLeast helpers for tiers are consistent with ordinal comparisons`() {
        // GIVEN
        val stage = PlayerRankedStage.GOLD_TWO

        // THEN
        assertTrue(actual = stage.isAtLeastBronzeOne)
        assertTrue(actual = stage.isAtLeastBronzeTwo)
        assertTrue(actual = stage.isAtLeastBronzeThree)

        assertTrue(actual = stage.isAtLeastSilverOne)
        assertTrue(actual = stage.isAtLeastSilverTwo)
        assertTrue(actual = stage.isAtLeastSilverThree)

        assertTrue(actual = stage.isAtLeastGoldOne)
        assertTrue(actual = stage.isAtLeastGoldTwo)
        assertFalse(actual = stage.isAtLeastGoldThree)

        assertFalse(actual = stage.isAtLeastDiamondOne)
        assertFalse(actual = stage.isAtLeastMythicOne)
        assertFalse(actual = stage.isAtLeastLegendaryOne)
        assertFalse(actual = stage.isAtLeastMasterOne)
    }
}
