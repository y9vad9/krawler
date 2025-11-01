package krawler.server.player.application.test.battle

import io.mockk.mockk
import krawler.server.player.application.PlayerName
import krawler.server.player.application.PlayerTag
import krawler.server.player.application.battle.BattleBot
import krawler.server.player.application.battle.FriendlyBattleBrawler
import krawler.server.player.application.battle.FriendlyBattleParticipants
import krawler.server.player.application.battle.FriendlyBattlePlayer
import krawler.server.player.application.battle.RankedBattleBrawler
import krawler.server.player.application.battle.RankedBattlePlayer
import krawler.server.player.application.battle.RankedBattlePlayers
import krawler.server.player.application.battle.TrophyLeagueBattleBrawler
import krawler.server.player.application.battle.TrophyLeagueBattlePlayer
import krawler.server.player.application.battle.TrophyLeagueBattlePlayers
import krawler.server.player.application.battle.getAsBotOrThrow
import krawler.server.player.application.battle.getAsPlayerOrThrow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class BattleParticipantsTest {

    private val playerTag1 = PlayerTag.createOrThrow("#TAG1")
    private val playerTag2 = PlayerTag.createOrThrow("#TAG2")
    private val playerName = PlayerName.createOrThrow("Player")
    private val friendlyBrawler = FriendlyBattleBrawler(
        id = mockk(relaxed = true),
        name = mockk(relaxed = true),
    )
    private val rankedBrawler = RankedBattleBrawler(
        id = mockk(relaxed = true),
        name = mockk(relaxed = true),
        powerLevel = mockk(relaxed = true),
        rankedStage = mockk(relaxed = true),
    )
    private val trophyBrawler = TrophyLeagueBattleBrawler(
        id = mockk(relaxed = true),
        name = mockk(relaxed = true),
        powerLevel = mockk(relaxed = true),
        trophies = mockk(relaxed = true),
    )

    private val friendlyPlayer = FriendlyBattlePlayer(playerTag1, playerName, friendlyBrawler)
    private val bot = BattleBot(playerTag2, playerName, friendlyBrawler)
    private val rankedPlayer = RankedBattlePlayer(playerTag1, playerName, rankedBrawler)
    private val trophyPlayer = TrophyLeagueBattlePlayer(playerTag1, playerName, trophyBrawler)

    @Test
    fun `friendly participants should retrieve players and bots correctly`() {
        // GIVEN a FriendlyBattleParticipants
        val participants = FriendlyBattleParticipants(listOf(friendlyPlayer, bot))

        // WHEN getting participant by tag
        assertEquals(friendlyPlayer, participants.getParticipantOrNull(playerTag1))
        assertEquals(bot, participants.getParticipantOrNull(playerTag2))
        assertNull(participants.getParticipantOrNull(PlayerTag.createOrThrow("#NONEXISTENT")))

        // THEN getAsPlayerOrThrow works
        assertEquals(friendlyPlayer, participants.getAsPlayerOrThrow(playerTag1))
        assertFailsWith<ClassCastException> { val _ = participants.getAsPlayerOrThrow(playerTag2) }

        // THEN getAsBotOrThrow works
        assertEquals(bot, participants.getAsBotOrThrow(playerTag2))
        assertFailsWith<ClassCastException> { val _ = participants.getAsBotOrThrow(playerTag1) }
    }

    @Test
    fun `ranked battle players should retrieve players by tag`() {
        // GIVEN a RankedBattlePlayers
        val participants = RankedBattlePlayers(listOf(rankedPlayer))

        // WHEN getting participant by tag
        assertEquals(rankedPlayer, participants.getParticipantOrNull(playerTag1))
        assertNull(participants.getParticipantOrNull(PlayerTag.createOrThrow("#NONEXISTENT")))

        // THEN getParticipantOrThrow throws if tag missing
        assertEquals(rankedPlayer, participants.getParticipantOrThrow(playerTag1))
        assertFailsWith<IllegalStateException> {
            val _ = participants.getParticipantOrThrow(PlayerTag.createOrThrow("#NONEXISTENT"))
        }
    }

    @Test
    fun `trophy league battle players should retrieve players by tag`() {
        // GIVEN a TrophyLeagueBattlePlayers
        val participants = TrophyLeagueBattlePlayers(listOf(trophyPlayer))

        // WHEN getting participant by tag
        assertEquals(trophyPlayer, participants.getParticipantOrNull(playerTag1))
        assertNull(participants.getParticipantOrNull(PlayerTag.createOrThrow("#NONEXISTENT")))

        // THEN getParticipantOrThrow throws if tag missing
        assertEquals(trophyPlayer, participants.getParticipantOrThrow(playerTag1))
        assertFailsWith<IllegalStateException> {
            val _ = participants.getParticipantOrThrow(PlayerTag.createOrThrow("#NONEXISTENT"))
        }
    }
}
