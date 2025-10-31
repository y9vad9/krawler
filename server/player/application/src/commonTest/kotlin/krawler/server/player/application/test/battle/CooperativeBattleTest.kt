package krawler.server.player.application.test.battle

import io.mockk.mockk
import krawler.server.player.application.battle.BattleEvent
import krawler.server.player.application.battle.BattleParticipants
import krawler.server.player.application.battle.BattleResult
import krawler.server.player.application.battle.EnemyBotsLevel
import krawler.server.player.application.battle.EventLastStandBattle
import krawler.server.player.application.battle.FriendlyLastStandBattle
import krawler.server.player.application.battle.OfficialBattleEvent
import krawler.server.player.application.battle.isEventCooperative
import krawler.server.player.application.battle.isEventLastStandBattle
import krawler.server.player.application.battle.isFriendlyCooperative
import krawler.server.player.application.battle.isFriendlyLastStandBattle
import krawler.server.player.application.battle.isLastStandBattle
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Instant

@Suppress("KotlinConstantConditions")
class CooperativeBattleTest {

    private val mockEvent = mockk<BattleEvent>()
    private val mockOfficialEvent = mockk<OfficialBattleEvent>()
    private val mockParticipants = mockk<BattleParticipants>()
    private val mockLevel = mockk<EnemyBotsLevel>()

    @Test
    fun `friendly Last Stand battle smart casts correctly`() {
        // GIVEN a friendly Last Stand battle
        val battle = FriendlyLastStandBattle(
            level = mockLevel,
            players = mockParticipants,
            time = Instant.fromEpochMilliseconds(0),
            event = mockEvent,
            result = BattleResult.VICTORY
        )

        // WHEN we check its type
        val isFriendlyCoop = battle.isFriendlyCooperative()
        val isEventCoop = battle.isEventCooperative()
        val isLastStand = battle.isLastStandBattle()
        val isFriendlyLastStand = battle.isFriendlyLastStandBattle()
        val isEventLastStand = battle.isEventLastStandBattle()

        // THEN it should be recognized as friendly and Last Stand
        assertTrue(isFriendlyCoop)
        assertFalse(isEventCoop)
        assertTrue(isLastStand)
        assertTrue(isFriendlyLastStand)
        assertFalse(isEventLastStand)
    }

    @Test
    fun `event Last Stand battle smart casts correctly`() {
        // GIVEN a competitive Last Stand battle
        val battle = EventLastStandBattle(
            level = mockLevel,
            players = mockk(relaxed = true),
            time = Instant.fromEpochMilliseconds(0),
            event = mockOfficialEvent,
            result = BattleResult.DEFEAT
        )

        // WHEN we check its type
        val isFriendlyCoop = battle.isFriendlyCooperative()
        val isEventCoop = battle.isEventCooperative()
        val isLastStand = battle.isLastStandBattle()
        val isFriendlyLastStand = battle.isFriendlyLastStandBattle()
        val isEventLastStand = battle.isEventLastStandBattle()

        // THEN it should be recognized as event and Last Stand
        assertFalse(isFriendlyCoop)
        assertTrue(isEventCoop)
        assertTrue(isLastStand)
        assertFalse(isFriendlyLastStand)
        assertTrue(isEventLastStand)
    }
}
