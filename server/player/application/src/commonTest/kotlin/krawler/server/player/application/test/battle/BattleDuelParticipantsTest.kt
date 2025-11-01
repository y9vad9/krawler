package krawler.server.player.application.test.battle

import io.mockk.mockk
import krawler.server.player.application.PlayerName
import krawler.server.player.application.PlayerTag
import krawler.server.player.application.battle.BattleDuelBot
import krawler.server.player.application.battle.FriendlyBattleBrawler
import krawler.server.player.application.battle.FriendlyBattleDuelPlayer
import krawler.server.player.application.battle.FriendlyDuelBattleParticipants
import krawler.server.player.application.battle.TrophyLeagueBattleBrawler
import krawler.server.player.application.battle.TrophyLeagueBattleDuelPlayer
import krawler.server.player.application.battle.TrophyLeagueBattleDuelPlayers
import krawler.server.player.application.battle.getAsBotOrNull
import krawler.server.player.application.battle.getAsBotOrThrow
import krawler.server.player.application.battle.getAsPlayerOrNull
import krawler.server.player.application.battle.getAsPlayerOrThrow
import krawler.server.player.application.battle.isBot
import krawler.server.player.application.battle.isFriendly
import krawler.server.player.application.battle.isPlayer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Suppress("KotlinConstantConditions") // contracts don't save us from mistakes
class BattleDuelParticipantsTest {

    private val playerTag1 = PlayerTag.createOrThrow("#P12")
    private val playerTag2 = PlayerTag.createOrThrow("#P23")
    private val botTag = PlayerTag.createOrThrow("#BOT")

    private val playerName1 = PlayerName.createOrThrow("Alice")
    private val playerName2 = PlayerName.createOrThrow("Bob")
    private val botName = PlayerName.createOrThrow("BotAlpha")

    private val friendlyBrawler = mockk<FriendlyBattleBrawler>()
    private val trophyBrawler = mockk<TrophyLeagueBattleBrawler>()

    private val friendlyPlayer1 = FriendlyBattleDuelPlayer(playerTag1, playerName1, listOf(friendlyBrawler))
    private val bot = BattleDuelBot(botTag, botName, listOf(friendlyBrawler))

    private val trophyPlayer1 = TrophyLeagueBattleDuelPlayer(playerTag1, playerName1, listOf(trophyBrawler))
    private val trophyPlayer2 = TrophyLeagueBattleDuelPlayer(playerTag2, playerName2, listOf(trophyBrawler))

    @Test
    fun `GIVEN FriendlyDuelBattleParticipants WHEN getting participants by tag THEN returns correct instances`() {
        // GIVEN
        val participants = FriendlyDuelBattleParticipants(listOf(friendlyPlayer1, bot))

        // WHEN & THEN
        assertEquals(friendlyPlayer1, participants.getParticipantOrThrow(playerTag1))
        assertEquals(bot, participants.getParticipantOrThrow(botTag))
        assertNull(participants.getParticipantOrNull(PlayerTag.createOrThrow("#UNKNOWN")))

        // Smart cast helpers
        assertTrue(participants.getAsPlayerOrThrow(playerTag1).isFriendly())
        assertEquals(bot, participants.getAsBotOrThrow(botTag))
        assertNull(participants.getAsPlayerOrNull(botTag))
        assertNull(participants.getAsBotOrNull(playerTag1))

        // first / second
        assertEquals(friendlyPlayer1, participants.first)
        assertEquals(bot, participants.second)

        // Exception when tag not found
        val unknownTag = PlayerTag.createOrThrow("#UNKNOWN")
        assertFailsWith<IllegalArgumentException> {
            val _ = participants.getParticipantOrThrow(unknownTag)
        }
        assertFailsWith<ClassCastException> {
            val _ = participants.getAsPlayerOrThrow(botTag)
        }
    }

    @Test
    fun `GIVEN TrophyLeagueBattleDuelPlayers WHEN getting participants by tag THEN returns correct instances`() {
        // GIVEN
        val participants = TrophyLeagueBattleDuelPlayers(listOf(trophyPlayer1, trophyPlayer2))

        // WHEN & THEN
        assertEquals(trophyPlayer1, participants.getParticipantOrThrow(playerTag1))
        assertEquals(trophyPlayer2, participants.getParticipantOrThrow(playerTag2))
        assertNull(participants.getParticipantOrNull(PlayerTag.createOrThrow("#UNKNOWN")))

        // first / second
        assertEquals(trophyPlayer1, participants.first)
        assertEquals(trophyPlayer2, participants.second)
    }

    @Test
    fun `GIVEN FriendlyDuelBattleParticipants with mixed types WHEN checking type helpers returns correct results`() {
        // GIVEN
        val participants = FriendlyDuelBattleParticipants(listOf(friendlyPlayer1, bot))

        // THEN
        val first = participants.list[0]
        val second = participants.list[1]

        assertTrue(first.isPlayer())
        assertFalse(first.isBot())
        assertFalse(second.isPlayer())
        assertTrue(second.isBot())
    }
}
