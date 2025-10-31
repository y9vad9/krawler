package krawler.server.player.application.test.battle

import krawler.server.player.application.PlayerName
import krawler.server.player.application.PlayerRankedStage
import krawler.server.player.application.PlayerTag
import krawler.server.player.application.Trophies
import krawler.server.player.application.battle.FriendlyBattleBrawler
import krawler.server.player.application.battle.FriendlyBattleStarPlayer
import krawler.server.player.application.battle.RankedBattleBrawler
import krawler.server.player.application.battle.RankedBattleStarPlayer
import krawler.server.player.application.battle.TrophyLeagueBattleBrawler
import krawler.server.player.application.battle.TrophyLeagueBattleStarPlayer
import krawler.server.player.application.battle.isFriendly
import krawler.server.player.application.battle.isRanked
import krawler.server.player.application.battle.isTrophyLeague
import krawler.server.player.application.brawler.BrawlerId
import krawler.server.player.application.brawler.BrawlerName
import krawler.server.player.application.brawler.BrawlerPowerLevel
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Suppress("KotlinConstantConditions")
class BattleStarPlayerTest {

    private val friendlyBrawler = FriendlyBattleBrawler(
        id = BrawlerId.SHELLY,
        name = BrawlerName("Shelly")
    )

    private val trophyBrawler = TrophyLeagueBattleBrawler(
        id = BrawlerId.COLT,
        name = BrawlerName("Colt"),
        powerLevel = BrawlerPowerLevel.createOrThrow(10),
        trophies = Trophies.createOrThrow(500)
    )

    private val rankedBrawler = RankedBattleBrawler(
        id = BrawlerId.COLT,
        name = BrawlerName("Colt"),
        powerLevel = BrawlerPowerLevel.createOrThrow(10),
        rankedStage = PlayerRankedStage(5)
    )

    private val friendlyPlayer = FriendlyBattleStarPlayer(
        tag = PlayerTag.createOrThrow("#FRIENDLY"),
        name = PlayerName.createOrThrow("Alice"),
        brawler = friendlyBrawler
    )

    private val trophyPlayer = TrophyLeagueBattleStarPlayer(
        tag = PlayerTag.createOrThrow("#TROPHY"),
        name = PlayerName.createOrThrow("Bob"),
        brawler = trophyBrawler
    )

    private val rankedPlayer = RankedBattleStarPlayer(
        tag = PlayerTag.createOrThrow("#RANKED"),
        name = PlayerName.createOrThrow("Charlie"),
        brawler = rankedBrawler
    )

    @Test
    fun `friendly battle star player should be recognized correctly`() {
        // GIVEN a friendly BattleStar player
        val player = friendlyPlayer

        // THEN
        assertTrue(player.isFriendly())
        assertFalse(player.isTrophyLeague())
        assertFalse(player.isRanked())
    }

    @Test
    fun `trophy league battle star player should be recognized correctly`() {
        // GIVEN a trophy league BattleStar player
        val player = trophyPlayer

        // THEN
        assertTrue(player.isTrophyLeague())
        assertFalse(player.isFriendly())
        assertFalse(player.isRanked())
    }

    @Test
    fun `ranked battle star player should be recognized correctly`() {
        // GIVEN a ranked BattleStar player
        val player = rankedPlayer

        // THEN
        assertTrue(player.isRanked())
        assertFalse(player.isFriendly())
        assertFalse(player.isTrophyLeague())
    }
}
