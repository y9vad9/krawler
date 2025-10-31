package krawler.server.player.application.test.battle

import io.mockk.mockk
import krawler.server.player.application.PlayerName
import krawler.server.player.application.PlayerTag
import krawler.server.player.application.battle.BattleBot
import krawler.server.player.application.battle.FriendlyBattleBrawler
import krawler.server.player.application.battle.FriendlyBattlePlayer
import krawler.server.player.application.battle.RankedBattleBrawler
import krawler.server.player.application.battle.RankedBattlePlayer
import krawler.server.player.application.battle.TrophyLeagueBattleBrawler
import krawler.server.player.application.battle.TrophyLeagueBattlePlayer
import krawler.server.player.application.battle.isBot
import krawler.server.player.application.battle.isFriendly
import krawler.server.player.application.battle.isPlayer
import krawler.server.player.application.battle.isRanked
import krawler.server.player.application.battle.isTrophyLeague
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Suppress("KotlinConstantConditions")
class BattleParticipantTest {

    private val tag = PlayerTag.createOrThrow("#123ABC")
    private val name = PlayerName.createOrThrow("PlayerOne")
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

    private val friendlyPlayer = FriendlyBattlePlayer(tag, name, friendlyBrawler)
    private val rankedPlayer = RankedBattlePlayer(tag, name, rankedBrawler)
    private val trophyPlayer = TrophyLeagueBattlePlayer(tag, name, trophyBrawler)
    private val bot = BattleBot(tag, name, friendlyBrawler)

    @Test
    fun `friendly player should be recognized correctly`() {
        // GIVEN a FriendlyBattlePlayer
        assertTrue(friendlyPlayer.isFriendly())
        assertFalse(friendlyPlayer.isRanked())
        assertFalse(friendlyPlayer.isTrophyLeague())
        assertTrue(friendlyPlayer.isPlayer())
        assertFalse(friendlyPlayer.isBot())
    }

    @Test
    fun `ranked player should be recognized correctly`() {
        // GIVEN a RankedBattlePlayer
        assertTrue(rankedPlayer.isRanked())
        assertFalse(rankedPlayer.isFriendly())
        assertFalse(rankedPlayer.isTrophyLeague())
        assertTrue(rankedPlayer.isPlayer())
        assertFalse(rankedPlayer.isBot())
    }

    @Test
    fun `trophy league player should be recognized correctly`() {
        // GIVEN a TrophyLeagueBattlePlayer
        assertTrue(trophyPlayer.isTrophyLeague())
        assertFalse(trophyPlayer.isFriendly())
        assertFalse(trophyPlayer.isRanked())
        assertTrue(trophyPlayer.isPlayer())
        assertFalse(trophyPlayer.isBot())
    }

    @Test
    fun `bot should be recognized correctly`() {
        // GIVEN a BattleBot
        assertTrue(bot.isBot())
        assertFalse(bot.isPlayer())
    }
}
