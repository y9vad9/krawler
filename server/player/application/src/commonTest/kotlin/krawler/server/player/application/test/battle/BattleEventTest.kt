package krawler.server.player.application.test.battle

import io.mockk.mockk
import krawler.server.player.application.battle.BattleEventId
import krawler.server.player.application.battle.BattleEventMode
import krawler.server.player.application.battle.MapMakerBattleEvent
import krawler.server.player.application.battle.OfficialBattleEvent
import krawler.server.player.application.battle.isMapMaker
import krawler.server.player.application.battle.isOfficial
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Suppress("KotlinConstantConditions")
class BattleEventTest {

    private val officialEventMode = BattleEventMode("bounty")
    private val mapMakerEventMode = BattleEventMode("duels")

    // instantiate the final data classes normally
    private val officialEvent = OfficialBattleEvent(
        id = BattleEventId.createOrThrow(15_000_000),
        mapName = mockk(relaxed = true),
        mode = officialEventMode,
    )
    private val mapMakerEvent = MapMakerBattleEvent(mode = mapMakerEventMode)

    @Test
    fun `official event should be recognized as official`() {
        // GIVEN officialEvent
        // WHEN checked
        val result = officialEvent.isOfficial()

        // THEN it returns true
        assertTrue(result)
        assertFalse(officialEvent.isMapMaker())
    }

    @Test
    fun `map maker event should be recognized as map maker`() {
        // GIVEN mapMakerEvent
        // WHEN checked
        val result = mapMakerEvent.isMapMaker()

        // THEN it returns true
        assertTrue(result)
        assertFalse(mapMakerEvent.isOfficial())
    }
}
