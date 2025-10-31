package krawler.server.player.application.test.battle

import io.mockk.mockk
import krawler.server.player.application.battle.BattleEvent
import krawler.server.player.application.battle.BattleResult
import krawler.server.player.application.battle.ClassicFriendlyDuelBattle
import krawler.server.player.application.battle.FriendlyDuelBattleParticipants
import krawler.server.player.application.battle.TrophyLeagueDuelBattle
import krawler.server.player.application.battle.isFriendly
import krawler.server.player.application.battle.isTrophyLeague
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Instant

@Suppress("KotlinConstantConditions")
class DuelBattleTest {

    private val mockParticipants = mockk<FriendlyDuelBattleParticipants>()
    private val mockEvent = mockk<BattleEvent>()

    @Test
    fun `classic friendly duel battle smart casts correctly`() {
        // GIVEN a friendly classic duel battle
        val duel = ClassicFriendlyDuelBattle(
            participants = mockParticipants,
            time = Instant.fromEpochMilliseconds(0),
            event = mockEvent,
            result = BattleResult.VICTORY
        )

        // WHEN we check its type
        val isFriendly = duel.isFriendly()
        val isTrophy = duel.isTrophyLeague()

        // THEN it should be recognized as friendly and not trophy league
        assertTrue(isFriendly)
        assertFalse(isTrophy)
    }

    @Test
    fun `trophy league duel battle smart casts correctly`() {
        // GIVEN a trophy league duel battle
        val duel = TrophyLeagueDuelBattle(
            participants = mockk(),
            time = Instant.fromEpochMilliseconds(0),
            event = mockEvent,
            result = BattleResult.DEFEAT
        )

        // WHEN we check its type
        val isFriendly = duel.isFriendly()
        val isTrophy = duel.isTrophyLeague()

        // THEN it should be recognized as trophy league and not friendly
        assertFalse(isFriendly)
        assertTrue(isTrophy)
    }
}
