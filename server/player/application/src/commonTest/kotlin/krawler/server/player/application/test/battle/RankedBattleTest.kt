package krawler.server.player.application.test.battle

import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import krawler.server.player.application.battle.ClassicFriendlyRankedBattle
import krawler.server.player.application.battle.FriendlyMapMakerRankedBattle
import krawler.server.player.application.battle.RankedLeagueBattle
import krawler.server.player.application.battle.RankedBattle
import krawler.server.player.application.battle.RankedBattle.Round
import krawler.server.player.application.battle.BattleResult
import krawler.server.player.application.battle.BattleEvent
import krawler.server.player.application.battle.MapMakerBattleEvent
import krawler.server.player.application.battle.FriendlyBattleStarPlayer
import krawler.server.player.application.battle.FriendlyBattleParticipants
import krawler.server.player.application.battle.RankedBattlePlayers
import krawler.server.player.application.battle.RankedBattleStarPlayer
import krawler.server.player.application.battle.RankedMatchmakingType
import krawler.server.player.application.battle.isDuoMatchmaking
import krawler.server.player.application.battle.isFinished
import krawler.server.player.application.battle.isFriendly
import krawler.server.player.application.battle.isFriendlyClassic
import krawler.server.player.application.battle.isFriendlyMapMaker
import krawler.server.player.application.battle.isLeague
import krawler.server.player.application.battle.isSoloMatchmaking
import krawler.server.player.application.battle.isTrioMatchmaking
import krawler.server.player.application.battle.totalDuration
import kotlin.test.assertEquals

class RankedBattleTest {

    private val sampleRounds = listOf(
        Round(BattleResult.VICTORY, 100.seconds),
        Round(BattleResult.DEFEAT, 200.seconds)
    )

    private val dummyEvent = mockk<BattleEvent>(relaxed = true)
    private val dummyMapMakerEvent = mockk<MapMakerBattleEvent>(relaxed = true)
    private val friendlyPlayer = mockk<FriendlyBattleStarPlayer>(relaxed = true)
    private val rankedStarPlayer = mockk<RankedBattleStarPlayer>(relaxed = true)
    private val firstTeamFriendly = mockk<FriendlyBattleParticipants>(relaxed = true)
    private val secondTeamFriendly = mockk<FriendlyBattleParticipants>(relaxed = true)
    private val firstTeamRanked = mockk<RankedBattlePlayers>(relaxed = true)
    private val secondTeamRanked = mockk<RankedBattlePlayers>(relaxed = true)

    @Test
    fun `GIVEN ClassicFriendlyRankedBattle WHEN checking smart-cast THEN isFriendly and isFriendlyClassic`() {
        // GIVEN
        val battle: RankedBattle = ClassicFriendlyRankedBattle(
            time = Instant.fromEpochMilliseconds(0),
            event = dummyEvent,
            rounds = sampleRounds,
            starPlayer = friendlyPlayer,
            result = BattleResult.VICTORY,
            matchmakingType = RankedMatchmakingType.SOLO,
            firstTeam = firstTeamFriendly,
            secondTeam = secondTeamFriendly
        )

        // WHEN & THEN
        assertTrue(battle.isFriendly())
        assertTrue(battle.isFriendlyClassic())

        val friendly = battle as? RankedBattle
        assertTrue(friendly?.isFriendlyClassic() ?: false)
    }

    @Test
    fun `isFriendlyMapMaker returns true on FriendlyMapMakerRankedBattle`() {
        // GIVEN
        val battle: RankedBattle = FriendlyMapMakerRankedBattle(
            time = Instant.fromEpochMilliseconds(0),
            event = dummyMapMakerEvent,
            rounds = sampleRounds,
            starPlayer = friendlyPlayer,
            result = BattleResult.DEFEAT,
            matchmakingType = RankedMatchmakingType.DUO,
            firstTeam = firstTeamFriendly,
            secondTeam = secondTeamFriendly
        )

        // WHEN & THEN
        assertTrue(battle.isFriendlyMapMaker())
    }

    @Test
    fun `GIVEN RankedLeagueBattle WHEN checking smart-cast THEN isLeague`() {
        // GIVEN
        val battle: RankedBattle = RankedLeagueBattle(
            time = Instant.fromEpochMilliseconds(0),
            event = mockk(relaxed = true),
            rounds = sampleRounds,
            firstTeam = firstTeamRanked,
            secondTeam = secondTeamRanked,
            starPlayer = rankedStarPlayer,
            result = BattleResult.VICTORY,
            matchmakingType = RankedMatchmakingType.TRIO
        )

        // WHEN & THEN
        assertTrue(battle.isLeague())
    }

    @Test
    fun `GIVEN RankedBattle rounds WHEN computing totalDuration THEN sums correctly`() {
        // GIVEN
        val battle: RankedBattle = ClassicFriendlyRankedBattle(
            time = Instant.fromEpochMilliseconds(0),
            event = dummyEvent,
            rounds = sampleRounds,
            starPlayer = friendlyPlayer,
            result = BattleResult.VICTORY,
            matchmakingType = RankedMatchmakingType.SOLO,
            firstTeam = firstTeamFriendly,
            secondTeam = secondTeamFriendly
        )

        // WHEN
        val total = battle.totalDuration

        // THEN
        assertEquals(total, 300.seconds)
    }

    @Test
    fun `GIVEN RankedBattle result is null WHEN checking isFinished THEN false`() {
        // GIVEN
        val battle: RankedBattle = ClassicFriendlyRankedBattle(
            time = Instant.fromEpochMilliseconds(0),
            event = dummyEvent,
            rounds = sampleRounds,
            starPlayer = friendlyPlayer,
            result = null,
            matchmakingType = RankedMatchmakingType.SOLO,
            firstTeam = firstTeamFriendly,
            secondTeam = secondTeamFriendly
        )

        // WHEN & THEN
        assertFalse(battle.isFinished)
    }

    @Test
    fun `GIVEN RankedBattle with matchmakingType WHEN checking flags THEN correct`() {
        // GIVEN
        val battleSolo: RankedBattle = ClassicFriendlyRankedBattle(
            time = Instant.fromEpochMilliseconds(0),
            event = dummyEvent,
            rounds = sampleRounds,
            starPlayer = friendlyPlayer,
            result = BattleResult.VICTORY,
            matchmakingType = RankedMatchmakingType.SOLO,
            firstTeam = firstTeamFriendly,
            secondTeam = secondTeamFriendly
        )

        val battleDuo: RankedBattle = ClassicFriendlyRankedBattle(
            time = Instant.fromEpochMilliseconds(0),
            event = dummyEvent,
            rounds = sampleRounds,
            starPlayer = friendlyPlayer,
            result = BattleResult.VICTORY,
            matchmakingType = RankedMatchmakingType.DUO,
            firstTeam = firstTeamFriendly,
            secondTeam = secondTeamFriendly
        )

        val battleTrio: RankedBattle = ClassicFriendlyRankedBattle(
            time = Instant.fromEpochMilliseconds(0),
            event = dummyEvent,
            rounds = sampleRounds,
            starPlayer = friendlyPlayer,
            result = BattleResult.VICTORY,
            matchmakingType = RankedMatchmakingType.TRIO,
            firstTeam = firstTeamFriendly,
            secondTeam = secondTeamFriendly
        )

        // THEN
        assertTrue(battleSolo.isSoloMatchmaking)
        assertFalse(battleSolo.isDuoMatchmaking)
        assertFalse(battleSolo.isTrioMatchmaking)

        assertTrue(battleDuo.isDuoMatchmaking)
        assertFalse(battleDuo.isSoloMatchmaking)
        assertFalse(battleDuo.isTrioMatchmaking)

        assertTrue(battleTrio.isTrioMatchmaking)
        assertFalse(battleTrio.isSoloMatchmaking)
        assertFalse(battleTrio.isDuoMatchmaking)
    }
}
