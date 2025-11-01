package krawler.server.player.application.test.battle

import io.mockk.mockk
import krawler.server.player.application.battle.ClassicFriendlySoloRankingBattle
import krawler.server.player.application.battle.RankingPosition
import krawler.server.player.application.battle.TrophyLeagueSoloRankingBattle
import krawler.server.player.application.battle.isFriendly
import krawler.server.player.application.battle.isTrophyLeague
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Instant

@Suppress("KotlinConstantConditions")
class SoloRankingBattleTest {

    @Test
    fun `isFriendly returns true for ClassicFriendlySoloRankingBattle`() {
        // GIVEN a classic friendly solo ranking battle
        val battle = ClassicFriendlySoloRankingBattle(
            time = Instant.fromEpochMilliseconds(0),
            participants = mockk(),
            rank = RankingPosition.FIRST,
            event = mockk(),
        )

        // THEN isFriendly should return true
        assertTrue(battle.isFriendly())
        assertFalse(battle.isTrophyLeague())
    }

    @Test
    fun `isTrophyLeague returns true for TrophyLeagueSoloRankingBattle`() {
        // GIVEN a trophy league solo ranking battle
        val battle = TrophyLeagueSoloRankingBattle(
            time = Instant.fromEpochMilliseconds(0),
            participants = mockk(relaxed = true),
            rank = RankingPosition.FIRST,
            event = mockk(relaxed = true),
            trophyChange = mockk(relaxed = true),
        )

        // THEN isTrophyLeague should return true
        assertTrue(battle.isTrophyLeague())
        assertFalse(battle.isFriendly())
    }

    @Test
    fun `isFriendly returns false for TrophyLeagueSoloRankingBattle`() {
        // GIVEN a trophy league solo ranking battle
        val battle = TrophyLeagueSoloRankingBattle(
            time = Instant.fromEpochMilliseconds(0),
            participants = mockk(relaxed = true),
            rank = RankingPosition.FIRST,
            event = mockk(relaxed = true),
            trophyChange = mockk(relaxed = true),
        )

        // THEN isFriendly should return false
        assertFalse(battle.isFriendly())
    }

    @Test
    fun `isTrophyLeague returns false for ClassicFriendlySoloRankingBattle`() {
        // GIVEN a classic friendly solo ranking battle
        val battle = ClassicFriendlySoloRankingBattle(
            time = Instant.fromEpochMilliseconds(0),
            participants = mockk(),
            rank = RankingPosition.FIRST,
            event = mockk(),
        )

        // THEN isTrophyLeague should return false
        assertFalse(battle.isTrophyLeague())
    }
}
