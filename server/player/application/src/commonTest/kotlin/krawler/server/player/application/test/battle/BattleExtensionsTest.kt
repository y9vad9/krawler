package krawler.server.player.application.test.battle

import io.mockk.every
import io.mockk.mockk
import krawler.server.player.application.DefeatAmount
import krawler.server.player.application.DrawAmount
import krawler.server.player.application.VictoryAmount
import krawler.server.player.application.battle.BattleEvent
import krawler.server.player.application.battle.BattleResult
import krawler.server.player.application.battle.CooperativeBattle
import krawler.server.player.application.battle.FriendlyBattle
import krawler.server.player.application.battle.MapMakerBattle
import krawler.server.player.application.battle.defeatsAmount
import krawler.server.player.application.battle.drawsOrNeutralsAmount
import krawler.server.player.application.battle.isDefeat
import krawler.server.player.application.battle.isDrawOrNeutral
import krawler.server.player.application.battle.isMapMaker
import krawler.server.player.application.battle.isVictory
import krawler.server.player.application.battle.victoriesAmount
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Instant

class BattleExtensionsTest {

    private val dummyEvent = mockk<BattleEvent>(relaxed = true)
    private val dummyInstant = Instant.fromEpochMilliseconds(1_000_000)

    @Test
    fun `isVictory returns true for CooperativeBattle`() {
        // GIVEN a mock CooperativeBattle
        val battle = mockk<CooperativeBattle> {
            every { result } returns BattleResult.VICTORY
            every { time } returns dummyInstant
            every { event } returns dummyEvent
        }

        // THEN isVictory is true
        assert(battle.isVictory)
    }

    @Test
    fun `isDefeat returns true for CooperativeBattle`() {
        val battle = mockk<CooperativeBattle> {
            every { result } returns BattleResult.DEFEAT
            every { time } returns dummyInstant
            every { event } returns dummyEvent
        }

        assertTrue(battle.isDefeat)
    }

    @Test
    fun `isDrawOrNeutral returns true for CooperativeBattle`() {
        val battle = mockk<CooperativeBattle> {
            every { result } returns BattleResult.DRAW
            every { time } returns dummyInstant
            every { event } returns dummyEvent
        }

        assertTrue(battle.isDrawOrNeutral)
    }

    @Test
    fun `list extensions correctly count victories, defeats, and draws`() {
        // GIVEN multiple battles with mixed outcomes
        val victoryBattle = mockk<CooperativeBattle> {
            every { result } returns BattleResult.VICTORY
            every { time } returns dummyInstant
            every { event } returns dummyEvent
        }

        val defeatBattle = mockk<CooperativeBattle> {
            every { result } returns BattleResult.DEFEAT
            every { time } returns dummyInstant
            every { event } returns dummyEvent
        }

        val drawBattle = mockk<CooperativeBattle> {
            every { result } returns BattleResult.DRAW
            every { time } returns dummyInstant
            every { event } returns dummyEvent
        }

        val battles = listOf(victoryBattle, defeatBattle, drawBattle)

        // THEN the aggregated counts are correct
        assertEquals(VictoryAmount.createOrThrow(1), battles.victoriesAmount)
        assertEquals(DefeatAmount.createOrThrow(1), battles.defeatsAmount)
        assertEquals(DrawAmount.createOrThrow(1), battles.drawsOrNeutralsAmount)
    }

    @Test
    fun `isMapMaker returns true only for MapMakerBattle`() {
        val mapMakerBattle = mockk<MapMakerBattle> {
            every { event } returns mockk(relaxed = true)
            every { time } returns dummyInstant
        }
        val friendlyBattle = mockk<FriendlyBattle> {
            every { event } returns dummyEvent
            every { time } returns dummyInstant
        }

        assert(mapMakerBattle.isMapMaker())
        assert(!friendlyBattle.isMapMaker())
    }
}
