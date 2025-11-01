package krawler.server.player.application.test.battle

import krawler.server.player.application.PlayerName
import krawler.server.player.application.PlayerTag
import krawler.server.player.application.Trophies
import krawler.server.player.application.battle.BattleDuelBot
import krawler.server.player.application.battle.BattleDuelParticipant
import krawler.server.player.application.battle.FriendlyBattleBrawler
import krawler.server.player.application.battle.FriendlyBattleDuelPlayer
import krawler.server.player.application.battle.TrophyLeagueBattleBrawler
import krawler.server.player.application.battle.TrophyLeagueBattleDuelPlayer
import krawler.server.player.application.battle.isBot
import krawler.server.player.application.battle.isFriendly
import krawler.server.player.application.battle.isPlayer
import krawler.server.player.application.battle.isTrophyLeague
import krawler.server.player.application.brawler.BrawlerId
import krawler.server.player.application.brawler.BrawlerName
import krawler.server.player.application.brawler.BrawlerPowerLevel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Suppress("KotlinConstantConditions") // contracts don't save us from mistakes
class BattleDuelParticipantTest {

    private val sampleTag = PlayerTag.createOrThrow("#PLAYER1")
    private val sampleName = PlayerName.createOrThrow("TestPlayer")

    private val friendlyBrawler = FriendlyBattleBrawler(
        id = BrawlerId.COLT,
        name = BrawlerName("Shelly"),
    )
    private val trophyBrawler = TrophyLeagueBattleBrawler(
        id = BrawlerId.SHELLY,
        name = BrawlerName("Colt"),
        trophies = Trophies(100),
        powerLevel = BrawlerPowerLevel.MAX,
    )

    @Test
    fun `GIVEN FriendlyBattleDuelPlayer WHEN isFriendly THEN returns true`() {
        // GIVEN
        val player = FriendlyBattleDuelPlayer(
            tag = sampleTag,
            name = sampleName,
            brawlers = listOf(friendlyBrawler)
        )

        // THEN
        assertTrue(player.isFriendly())
        assertFalse(player.isTrophyLeague())
        assertTrue(player.isPlayer())
        assertFalse((player as BattleDuelParticipant).isBot())
    }

    @Test
    fun `GIVEN TrophyLeagueBattleDuelPlayer WHEN isTrophyLeague THEN returns true`() {
        // GIVEN
        val player = TrophyLeagueBattleDuelPlayer(
            tag = sampleTag,
            name = sampleName,
            brawlers = listOf(trophyBrawler)
        )

        // THEN
        assertTrue(player.isTrophyLeague())
        assertFalse(player.isFriendly())
        assertTrue(player.isPlayer())
        assertFalse((player as BattleDuelParticipant).isBot())
    }

    @Test
    fun `GIVEN BattleDuelBot WHEN isBot THEN returns true`() {
        // GIVEN
        val bot = BattleDuelBot(
            tag = sampleTag,
            name = sampleName,
            brawlers = listOf(friendlyBrawler)
        )

        // THEN
        assertTrue(bot.isBot())
        assertFalse(bot.isPlayer())
    }

    @Test
    fun `GIVEN list of mixed participants WHEN checking types THEN type detection works`() {
        // GIVEN
        val participants: List<BattleDuelParticipant> = listOf(
            FriendlyBattleDuelPlayer(sampleTag, sampleName, listOf(friendlyBrawler)),
            TrophyLeagueBattleDuelPlayer(sampleTag, sampleName, listOf(trophyBrawler)),
            BattleDuelBot(sampleTag, sampleName, listOf(friendlyBrawler))
        )

        // THEN
        assertEquals(2, participants.count { it.isPlayer() })
        assertEquals(1, participants.count { it.isBot() })
        assertEquals(1, participants.count { it.isPlayer() && it.isFriendly() })
        assertEquals(1, participants.count { it.isPlayer() && it.isTrophyLeague() })
    }
}
