package krawler.server.player.application.test.battle

import krawler.server.player.application.PlayerRankedStage
import krawler.server.player.application.Trophies
import krawler.server.player.application.battle.BattleBrawler
import krawler.server.player.application.battle.FriendlyBattleBrawler
import krawler.server.player.application.battle.RankedBattleBrawler
import krawler.server.player.application.battle.TrophyLeagueBattleBrawler
import krawler.server.player.application.battle.isInFriendly
import krawler.server.player.application.battle.isInRanked
import krawler.server.player.application.battle.isInTrophyLeague
import krawler.server.player.application.brawler.BrawlerId
import krawler.server.player.application.brawler.BrawlerName
import krawler.server.player.application.brawler.BrawlerPowerLevel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

@Suppress("KotlinConstantConditions")
class BattleBrawlerTest {

    private val dummyId = BrawlerId.createOrThrow(16_000_100)
    private val dummyName = BrawlerName("Shelly")
    private val dummyPower = BrawlerPowerLevel.createOrThrow(10)
    private val dummyTrophies = Trophies.createOrThrow(500)
    private val dummyRankedStage = PlayerRankedStage(20)

    @Test
    fun `RankedBattleBrawler properties are correct`() {
        // GIVEN a RankedBattleBrawler
        val brawler = RankedBattleBrawler(
            id = dummyId,
            name = dummyName,
            powerLevel = dummyPower,
            rankedStage = dummyRankedStage
        )

        // THEN properties match constructor
        assertEquals(dummyId, brawler.id)
        assertEquals(dummyName, brawler.name)
        assertEquals(dummyPower, brawler.powerLevel)
        assertEquals(dummyRankedStage, brawler.rankedStage)

        // AND type-check functions work
        assertTrue(brawler.isInRanked())
        assertFalse(brawler.isInFriendly())
        assertFalse(brawler.isInTrophyLeague())
    }

    @Test
    fun `TrophyLeagueBattleBrawler properties are correct`() {
        // GIVEN a TrophyLeagueBattleBrawler
        val brawler = TrophyLeagueBattleBrawler(
            id = dummyId,
            name = dummyName,
            powerLevel = dummyPower,
            trophies = dummyTrophies
        )

        // THEN properties match constructor
        assertEquals(dummyId, brawler.id)
        assertEquals(dummyName, brawler.name)
        assertEquals(dummyPower, brawler.powerLevel)
        assertEquals(dummyTrophies, brawler.trophies)

        // AND type-check functions work
        assertTrue(brawler.isInTrophyLeague())
        assertFalse(brawler.isInFriendly())
        assertFalse(brawler.isInRanked())
    }

    @Test
    fun `FriendlyBattleBrawler properties are correct`() {
        // GIVEN a FriendlyBattleBrawler
        val brawler = FriendlyBattleBrawler(
            id = dummyId,
            name = dummyName
        )

        // THEN id and name match constructor
        assertEquals(dummyId, brawler.id)
        assertEquals(dummyName, brawler.name)

        // AND powerLevel is always MAX
        assertEquals(BrawlerPowerLevel.MAX, brawler.powerLevel)

        // AND type-check functions work
        assertTrue(brawler.isInFriendly())
        assertFalse(brawler.isInTrophyLeague())
        assertFalse(brawler.isInRanked())
    }

    @Test
    fun `smart cast works with isInRanked`() {
        val brawler: BattleBrawler = RankedBattleBrawler(dummyId, dummyName, dummyPower, dummyRankedStage)

        if (brawler.isInRanked()) {
            // Smart cast to RankedBattleBrawler
            val rankedBrawler: RankedBattleBrawler = brawler
            assertEquals(dummyRankedStage, rankedBrawler.rankedStage)
        }
    }

    @Test
    fun `smart cast works with isInTrophyLeague`() {
        val brawler: BattleBrawler = TrophyLeagueBattleBrawler(dummyId, dummyName, dummyPower, dummyTrophies)

        if (brawler.isInTrophyLeague()) {
            val trophyBrawler: TrophyLeagueBattleBrawler = brawler
            assertEquals(dummyTrophies, trophyBrawler.trophies)
        }
    }

    @Test
    fun `smart cast works with isInFriendly`() {
        val brawler: BattleBrawler = FriendlyBattleBrawler(dummyId, dummyName)

        if (brawler.isInFriendly()) {
            val friendlyBrawler: FriendlyBattleBrawler = brawler
            assertEquals(BrawlerPowerLevel.MAX, friendlyBrawler.powerLevel)
        }
    }
}
