package krawler.server.player.application.test.battle

import io.mockk.mockk
import krawler.server.player.application.battle.ClassicFriendlyTeamRankingBattle
import krawler.server.player.application.battle.RankingPosition
import krawler.server.player.application.battle.TrophyLeagueTeamRankingBattle
import krawler.server.player.application.battle.isFriendly
import krawler.server.player.application.battle.isTrophyLeague
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.time.Instant

@Suppress("KotlinConstantConditions")
class TeamRankingBattleTest {

    @Test
    fun `isFriendly returns true for ClassicFriendlyTeamRankingBattle`() {
        // GIVEN a classic friendly team ranking battle
        val battle = ClassicFriendlyTeamRankingBattle(
            time = Instant.fromEpochMilliseconds(0),
            rank = RankingPosition.FIRST,
            event = mockk(),
            teams = mockk(),
        )

        // THEN isFriendly should return true
        assertTrue(battle.isFriendly())
        assertFalse(battle.isTrophyLeague())
    }

    @Test
    fun `isTrophyLeague returns true for TrophyLeagueTeamRankingBattle`() {
        // GIVEN a trophy league team ranking battle
        val battle = TrophyLeagueTeamRankingBattle(
            time = Instant.fromEpochMilliseconds(0),
            rank = RankingPosition.FIRST,
            event = mockk(relaxed = true),
            teams = mockk(relaxed = true),
            trophyChange = mockk(relaxed = true),
        )

        // THEN isTrophyLeague should return true
        assertTrue(battle.isTrophyLeague())
        assertFalse(battle.isFriendly())
    }

    @Test
    fun `isFriendly returns false for TrophyLeagueTeamRankingBattle`() {
        // GIVEN a trophy league team ranking battle
        val battle = TrophyLeagueTeamRankingBattle(
            time = Instant.fromEpochMilliseconds(0),
            rank = RankingPosition.FIRST,
            event = mockk(relaxed = true),
            teams = mockk(relaxed = true),
            trophyChange = mockk(relaxed = true)
        )

        // THEN isFriendly should return false
        assertFalse(battle.isFriendly())
    }

    @Test
    fun `isTrophyLeague returns false for ClassicFriendlyTeamRankingBattle`() {
        // GIVEN a classic friendly team ranking battle
        val battle = ClassicFriendlyTeamRankingBattle(
            time = Instant.fromEpochMilliseconds(0),
            rank = RankingPosition.FIRST,
            event = mockk(),
            teams = mockk()
        )

        // THEN isTrophyLeague should return false
        assertFalse(battle.isTrophyLeague())
    }
}
