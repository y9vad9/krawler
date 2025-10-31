package krawler.server.player.application.test.battle

import io.mockk.every
import io.mockk.mockk
import krawler.server.player.application.PlayerTag
import krawler.server.player.application.battle.BattleBot
import krawler.server.player.application.battle.FriendlyBattleParticipants
import krawler.server.player.application.battle.FriendlyBattlePlayer
import krawler.server.player.application.battle.FriendlyBattleTeamsParticipants
import krawler.server.player.application.battle.RankedBattlePlayer
import krawler.server.player.application.battle.RankedBattlePlayers
import krawler.server.player.application.battle.RankedBattleTeamsPlayers
import krawler.server.player.application.battle.TrophyLeagueBattlePlayer
import krawler.server.player.application.battle.TrophyLeagueBattlePlayers
import krawler.server.player.application.battle.TrophyLeagueBattleTeamsPlayers
import krawler.server.player.application.battle.findBotOrNull
import krawler.server.player.application.battle.findBotOrThrow
import krawler.server.player.application.battle.findPlayerOrNull
import krawler.server.player.application.battle.findPlayerOrThrow
import krawler.server.player.application.battle.firstTeam
import krawler.server.player.application.battle.secondTeamOrNull
import krawler.server.player.application.battle.secondTeamOrThrow
import krawler.server.player.application.battle.thirdTeamOrNull
import krawler.server.player.application.battle.thirdTeamOrThrow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class BattleTeamsParticipantsTest {

    private val player1 = mockk<FriendlyBattlePlayer> {
        every { tag } returns PlayerTag.createOrThrow("#P12")
    }
    private val player2 = mockk<FriendlyBattlePlayer> {
        every { tag } returns PlayerTag.createOrThrow("#P23")
    }
    private val bot1 = mockk<BattleBot> {
        every { tag } returns PlayerTag.createOrThrow("#B12")
    }

    private val friendlyTeam1 = FriendlyBattleParticipants(listOf(player1, bot1))
    private val friendlyTeam2 = FriendlyBattleParticipants(listOf(player2))

    private val friendlyTeams = FriendlyBattleTeamsParticipants(listOf(friendlyTeam1, friendlyTeam2))

    private val rankedPlayer1 = mockk<RankedBattlePlayer> {
        every { tag } returns PlayerTag.createOrThrow("#RP1")
    }
    private val rankedTeam = RankedBattlePlayers(listOf(rankedPlayer1))
    private val rankedTeams = RankedBattleTeamsPlayers(listOf(rankedTeam))

    private val trophyPlayer1 = mockk<TrophyLeagueBattlePlayer> {
        every { tag } returns PlayerTag.createOrThrow("#TP1")
    }
    private val trophyTeam = TrophyLeagueBattlePlayers(listOf(trophyPlayer1))
    private val trophyTeams = TrophyLeagueBattleTeamsPlayers(listOf(trophyTeam))

    @Test
    fun `should retrieve friendly participant by tag`() {
        // GIVEN friendlyTeams
        val tag = PlayerTag.createOrThrow("#P12")

        // WHEN
        val participant = friendlyTeams.findParticipantOrNull(tag)
        val player = friendlyTeams.findPlayerOrNull(tag)
        val bot = friendlyTeams.findBotOrNull(PlayerTag.createOrThrow("#B12"))

        // THEN
        assertEquals(player1, participant)
        assertEquals(player1, player)
        assertEquals(bot1, bot)
        assertNull(friendlyTeams.findParticipantOrNull(PlayerTag.createOrThrow("#UNKNOWN")))
    }

    @Test
    fun `should throw when participant not found`() {
        // GIVEN friendlyTeams
        val tag = PlayerTag.createOrThrow("#UNKNOWN")

        // THEN
        assertFailsWith<IllegalStateException> {
            val _ = friendlyTeams.findParticipantOrThrow(tag)
        }
        assertFailsWith<IllegalStateException> {
            val _ = friendlyTeams.findPlayerOrThrow(tag)
        }
        assertFailsWith<IllegalStateException> {
            val _ = friendlyTeams.findBotOrThrow(tag)
        }
    }

    @Test
    fun `should get team by participant tag`() {
        // GIVEN friendlyTeams

        // WHEN
        val team1 = friendlyTeams.getTeamOrNull(PlayerTag.createOrThrow("#P12"))
        val team2 = friendlyTeams.getTeamOrThrow(PlayerTag.createOrThrow("#P23"))

        // THEN
        assertEquals(friendlyTeam1, team1)
        assertEquals(friendlyTeam2, team2)
        assertNull(friendlyTeams.getTeamOrNull(PlayerTag.createOrThrow("#UNKNOWN")))
        assertFailsWith<IllegalStateException> {
            val _ = friendlyTeams.getTeamOrThrow(PlayerTag.createOrThrow("#UNKNOWN"))
        }
    }

    @Test
    fun `should retrieve ranked participant by tag`() {
        // GIVEN rankedTeams
        val tag = PlayerTag.createOrThrow("#RP1")

        // WHEN
        val participant = rankedTeams.findParticipantOrNull(tag)

        // THEN
        assertEquals(rankedPlayer1, participant)
        assertNull(rankedTeams.findParticipantOrNull(PlayerTag.createOrThrow("#UNKNOWN")))
    }

    @Test
    fun `should retrieve trophy participant by tag`() {
        // GIVEN trophyTeams
        val tag = PlayerTag.createOrThrow("#TP1")

        // WHEN
        val participant = trophyTeams.findParticipantOrNull(tag)

        // THEN
        assertEquals(trophyPlayer1, participant)
        assertNull(trophyTeams.findParticipantOrNull(PlayerTag.createOrThrow("#UNKNOWN")))
    }

    @Test
    fun `first, second, third teams extension functions`() {
        // GIVEN friendlyTeams

        // THEN
        assertEquals(friendlyTeam1, friendlyTeams.firstTeam())
        assertEquals(friendlyTeam2, friendlyTeams.secondTeamOrNull())
        assertEquals(friendlyTeam2, friendlyTeams.secondTeamOrThrow())
        assertNull(friendlyTeams.thirdTeamOrNull())
        assertFailsWith<IndexOutOfBoundsException> {
            val _ = friendlyTeams.thirdTeamOrThrow()
        }
    }
}
