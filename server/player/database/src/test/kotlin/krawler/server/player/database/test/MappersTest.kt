package krawler.server.player.database.test

import io.mockk.every
import io.mockk.mockk
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import krawler.server.player.database.BattleResult
import krawler.server.player.database.BattleType
import krawler.server.player.database.PlayerBattleSnapshotsTable
import krawler.server.player.database.PlayerBattleTable
import krawler.server.player.database.PlayerBrawlerTable
import krawler.server.player.database.PlayerDatabaseConfig
import krawler.server.player.database.PlayerSnapshotsTable
import krawler.server.player.database.PlayerTable
import krawler.server.player.database.TeamPlayer
import krawler.server.player.database.toDbPlayer
import krawler.server.player.database.toDbPlayerBattle
import krawler.server.player.database.toDbPlayerBattleSnapshot
import krawler.server.player.database.toDbPlayerBrawler
import org.jetbrains.exposed.v1.core.ResultRow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

class MappersTest {

    private val config = PlayerDatabaseConfig(
        playerTagMaxLength = 16,
        clubTagMaxLength = 16,
        playerNameMaxLength = 32,
    )
    private val table = PlayerTable(config)

    @Test
    fun `should map all non-null fields correctly`() {
        // GIVEN
        val now = Clock.System.now()
        val row = mockk<ResultRow>()

        every { row[table.tag] } returns "#ABC123"
        every { row[table.clubTag] } returns "#CLUB42"
        every { row[table.name] } returns "BrawlerKing"
        every { row[table.nameColor] } returns "#FF00FF"
        every { row[table.currentTrophies] } returns 5000
        every { row[table.highestTrophies] } returns 5500
        every { row[table.currentRankedStage] } returns 10
        every { row[table.highestRankedStage] } returns 15
        every { row[table.soloVictories] } returns 100
        every { row[table.duoVictories] } returns 200
        every { row[table.trioVictories] } returns 300
        every { row[table.quintetVictories] } returns 400
        every { row[table.bestRoboRumbleTime] } returns 120.seconds
        every { row[table.bestTimeAsBigBrawler] } returns 240.seconds
        every { row[table.expLevel] } returns 12
        every { row[table.expPoints] } returns 9000
        every { row[table.brawlersAmount] } returns 45
        every { row[table.currentBestBrawlerId] } returns 101
        every { row[table.currentFavoriteBrawlerId] } returns 202
        every { row[table.actualizedAt] } returns now

        // WHEN
        val result = row.toDbPlayer(table)

        // THEN
        assertEquals("#ABC123", result.tag)
        assertEquals("#CLUB42", result.clubTag)
        assertEquals("BrawlerKing", result.name)
        assertEquals("#FF00FF", result.nameColor)
        assertEquals(5000, result.currentTrophies)
        assertEquals(5500, result.highestTrophies)
        assertEquals(10, result.currentRankedStage)
        assertEquals(15, result.highestRankedStage)
        assertEquals(100, result.soloVictories)
        assertEquals(200, result.duoVictories)
        assertEquals(300, result.trioVictories)
        assertEquals(400, result.quintetVictories)
        assertEquals(120.seconds, result.bestRoboRumbleTime)
        assertEquals(240.seconds, result.bestTimeAsBigBrawler)
        assertEquals(12, result.expLevel)
        assertEquals(9000, result.expPoints)
        assertEquals(45, result.brawlersAmount)
        assertEquals(101, result.currentBestBrawlerId)
        assertEquals(202, result.currentFavoriteBrawlerId)
        assertEquals(now, result.actualizedAt)
    }

    @Test
    fun `should handle nullable fields correctly`() {
        // GIVEN
        val now = Clock.System.now()
        val row = mockk<ResultRow>()

        every { row[table.tag] } returns "#NULL1"
        every { row[table.clubTag] } returns null
        every { row[table.name] } returns "LonelyPlayer"
        every { row[table.nameColor] } returns "#000000"
        every { row[table.currentTrophies] } returns 1234
        every { row[table.highestTrophies] } returns 2345
        every { row[table.currentRankedStage] } returns null
        every { row[table.highestRankedStage] } returns null
        every { row[table.soloVictories] } returns 10
        every { row[table.duoVictories] } returns 20
        every { row[table.trioVictories] } returns 30
        every { row[table.quintetVictories] } returns 40
        every { row[table.bestRoboRumbleTime] } returns null
        every { row[table.bestTimeAsBigBrawler] } returns null
        every { row[table.expLevel] } returns 5
        every { row[table.expPoints] } returns 600
        every { row[table.brawlersAmount] } returns 10
        every { row[table.currentBestBrawlerId] } returns 5
        every { row[table.currentFavoriteBrawlerId] } returns 9
        every { row[table.actualizedAt] } returns now

        // WHEN
        val result = row.toDbPlayer(table)

        // THEN
        assertEquals("#NULL1", result.tag)
        assertEquals(null, result.clubTag)
        assertEquals("LonelyPlayer", result.name)
        assertEquals("#000000", result.nameColor)
        assertEquals(1234, result.currentTrophies)
        assertEquals(2345, result.highestTrophies)
        assertEquals(null, result.currentRankedStage)
        assertEquals(null, result.highestRankedStage)
        assertEquals(10, result.soloVictories)
        assertEquals(20, result.duoVictories)
        assertEquals(30, result.trioVictories)
        assertEquals(40, result.quintetVictories)
        assertEquals(null, result.bestRoboRumbleTime)
        assertEquals(null, result.bestTimeAsBigBrawler)
        assertEquals(5, result.expLevel)
        assertEquals(600, result.expPoints)
        assertEquals(10, result.brawlersAmount)
        assertEquals(5, result.currentBestBrawlerId)
        assertEquals(9, result.currentFavoriteBrawlerId)
        assertEquals(now, result.actualizedAt)
    }

    @Test
    fun `should map all non-null fields correctly for player snapshot`() {
        // GIVEN
        val json = Json
        val config = PlayerDatabaseConfig(playerTagMaxLength = 16, clubTagMaxLength = 16, playerNameMaxLength = 32)
        val table = PlayerSnapshotsTable(json, config)
        val row = mockk<ResultRow>()
        val now = Clock.System.now()
        val jsonObject = buildJsonObject { put("test", JsonPrimitive("value")) }

        every { row[table.tag] } returns "#ABC123"
        every { row[table.clubTag] } returns "#CLUB99"
        every { row[table.responseJson] } returns jsonObject
        every { row[table.fetchTime] } returns now

        // WHEN
        val result = row.toDbPlayer(table)

        // THEN
        assertEquals("#ABC123", result.tag)
        assertEquals("#CLUB99", result.clubTag)
        assertEquals(jsonObject, result.responseJson)
        assertEquals(now, result.fetchTime)
    }

    @Test
    fun `should map nullable clubTag correctly for player snapshot`() {
        // GIVEN
        val json = Json
        val config = PlayerDatabaseConfig(playerTagMaxLength = 16, clubTagMaxLength = 16, playerNameMaxLength = 32)
        val table = PlayerSnapshotsTable(json, config)
        val row = mockk<ResultRow>()
        val now = Clock.System.now()
        val jsonObject = buildJsonObject { put("player", JsonNull) }

        every { row[table.tag] } returns "#NOCLUB"
        every { row[table.clubTag] } returns null
        every { row[table.responseJson] } returns jsonObject
        every { row[table.fetchTime] } returns now

        // WHEN
        val result = row.toDbPlayer(table)

        // THEN
        assertEquals("#NOCLUB", result.tag)
        assertEquals(null, result.clubTag)
        assertEquals(jsonObject, result.responseJson)
        assertEquals(now, result.fetchTime)
    }

    @Test
    fun `should map all non-nullable and nullable fields correctly for player brawler`() {
        // GIVEN
        val config = PlayerDatabaseConfig(
            playerTagMaxLength = 16,
            clubTagMaxLength = 16,
            playerNameMaxLength = 32,
        )
        val table = PlayerBrawlerTable(config)
        val row = mockk<ResultRow>()

        val gears = listOf(1, 2, 3)
        val starPowers = listOf(4, 5)
        val gadgets = listOf(6)
        val currentWinStreak = 10
        val highestWinStreak = 15

        every { row[table.playerTag] } returns "#PLAYER1"
        every { row[table.brawlerId] } returns 123
        every { row[table.powerLevel] } returns 9
        every { row[table.rank] } returns 25
        every { row[table.trophies] } returns 600
        every { row[table.highestTrophies] } returns 650
        every { row[table.gears] } returns gears
        every { row[table.starPowers] } returns starPowers
        every { row[table.gadgets] } returns gadgets
        every { row[table.currentWinStreak] } returns currentWinStreak
        every { row[table.highestWinStreak] } returns highestWinStreak

        // WHEN
        val result = row.toDbPlayerBrawler(table)

        // THEN
        assertEquals("#PLAYER1", result.playerTag)
        assertEquals(123, result.brawlerId)
        assertEquals(9, result.powerLevel)
        assertEquals(25, result.rank)
        assertEquals(600, result.trophies)
        assertEquals(650, result.highestTrophies)
        assertEquals(gears, result.gears)
        assertEquals(starPowers, result.starPowers)
        assertEquals(gadgets, result.gadgets)
        assertEquals(currentWinStreak, result.currentWinStreak)
        assertEquals(highestWinStreak, result.highestWinStreak)
    }

    @Test
    fun `should handle nullable win streaks correctly`() {
        // GIVEN
        val config = PlayerDatabaseConfig(
            playerTagMaxLength = 16,
            clubTagMaxLength = 16,
            playerNameMaxLength = 32,
        )
        val table = PlayerBrawlerTable(config)
        val row = mockk<ResultRow>()

        val gears = emptyList<Int>()
        val starPowers = listOf(7, 8, 9)
        val gadgets = emptyList<Int>()

        every { row[table.playerTag] } returns "#NO_WIN"
        every { row[table.brawlerId] } returns 999
        every { row[table.powerLevel] } returns 5
        every { row[table.rank] } returns 10
        every { row[table.trophies] } returns 200
        every { row[table.highestTrophies] } returns 300
        every { row[table.gears] } returns gears
        every { row[table.starPowers] } returns starPowers
        every { row[table.gadgets] } returns gadgets
        every { row[table.currentWinStreak] } returns null
        every { row[table.highestWinStreak] } returns null

        // WHEN
        val result = row.toDbPlayerBrawler(table)

        // THEN
        assertEquals("#NO_WIN", result.playerTag)
        assertEquals(999, result.brawlerId)
        assertEquals(5, result.powerLevel)
        assertEquals(10, result.rank)
        assertEquals(200, result.trophies)
        assertEquals(300, result.highestTrophies)
        assertEquals(gears, result.gears)
        assertEquals(starPowers, result.starPowers)
        assertEquals(gadgets, result.gadgets)
        assertEquals(null, result.currentWinStreak)
        assertEquals(null, result.highestWinStreak)
    }

    @Test
    fun `should handle empty collections correctly`() {
        // GIVEN
        val config = PlayerDatabaseConfig(
            playerTagMaxLength = 16,
            clubTagMaxLength = 16,
            playerNameMaxLength = 32,
        )
        val table = PlayerBrawlerTable(config)
        val row = mockk<ResultRow>()

        every { row[table.playerTag] } returns "#EMPTY"
        every { row[table.brawlerId] } returns 42
        every { row[table.powerLevel] } returns 1
        every { row[table.rank] } returns 1
        every { row[table.trophies] } returns 0
        every { row[table.highestTrophies] } returns 0
        every { row[table.gears] } returns emptyList()
        every { row[table.starPowers] } returns emptyList()
        every{ row[table.gadgets] } returns emptyList()
        every { row[table.currentWinStreak] } returns null
        every { row[table.highestWinStreak] } returns null

        // WHEN
        val result = row.toDbPlayerBrawler(table)

        // THEN
        assertEquals("#EMPTY", result.playerTag)
        assertEquals(42, result.brawlerId)
        assertEquals(1, result.powerLevel)
        assertEquals(1, result.rank)
        assertEquals(0, result.trophies)
        assertEquals(0, result.highestTrophies)
        assertEquals(emptyList(), result.gears)
        assertEquals(emptyList(), result.starPowers)
        assertEquals(emptyList(), result.gadgets)
        assertEquals(null, result.currentWinStreak)
        assertEquals(null, result.highestWinStreak)
    }

    @Test
    fun `should map all non-null fields correctly for player battle`() {
        // GIVEN
        val json = Json
        val config = PlayerDatabaseConfig(playerTagMaxLength = 16, clubTagMaxLength = 16, playerNameMaxLength = 32)
        val table = PlayerBattleTable(config, json)
        val row = mockk<ResultRow>()

        val now = Clock.System.now()
        val battleTime = now.minus(60.seconds)
        val teams = listOf(listOf("#P1", "#P2"), listOf("#E1", "#E2"))
        val participants = listOf(
            TeamPlayer("#P1", "Alice", 10, TeamPlayer.Brawler(1, 10, 500, 2), 5),
            TeamPlayer("#P2", "Bob", 9, TeamPlayer.Brawler(2, 9, 480, 1), -3)
        )

        every { row[table.gameModeId] } returns 7
        every { row[table.eventId] } returns 101
        every { row[table.battleType] } returns BattleType.TROPHY_LEAGUE
        every { row[table.starPlayerTag] } returns "#STAR123"
        every { row[table.teams] } returns teams
        every { row[table.duration] } returns 120.seconds
        every { row[table.participants] } returns participants
        every { row[table.battleResult] } returns BattleResult.VICTORY
        every { row[table.battleTime] } returns battleTime
        every { row[table.fetchTime] } returns now

        // WHEN
        val result = row.toDbPlayerBattle(table)

        // THEN
        assertEquals(7, result.gameModeId)
        assertEquals(101, result.eventId)
        assertEquals(BattleType.TROPHY_LEAGUE, result.battleType)
        assertEquals("#STAR123", result.starPlayerTag)
        assertEquals(teams, result.teams)
        assertEquals(120.seconds, result.duration)
        assertEquals(participants, result.participants)
        assertEquals(BattleResult.VICTORY, result.battleResult)
        assertEquals(battleTime, result.battleTime)
        assertEquals(now, result.fetchTime)
    }

    @Test
    fun `should handle nullable teams field correctly`() {
        // GIVEN
        val json = Json
        val config = PlayerDatabaseConfig(playerTagMaxLength = 16, clubTagMaxLength = 16, playerNameMaxLength = 32)
        val table = PlayerBattleTable(config, json)
        val row = mockk<ResultRow>()

        val now = Clock.System.now()
        val participants = listOf(
            TeamPlayer("#A1", "Test", 8, TeamPlayer.Brawler(3, 8, 400, 1), null)
        )

        every { row[table.gameModeId] } returns 12
        every { row[table.eventId] } returns 222
        every { row[table.battleType] } returns BattleType.RANKED_LEAGUE
        every { row[table.starPlayerTag] } returns "#STAR999"
        every { row[table.teams] } returns null
        every { row[table.duration] } returns 90.seconds
        every { row[table.participants] } returns participants
        every { row[table.battleResult] } returns BattleResult.DEFEAT
        every { row[table.battleTime] } returns now.minus(120.seconds)
        every { row[table.fetchTime] } returns now

        // WHEN
        val result = row.toDbPlayerBattle(table)

        // THEN
        assertEquals(12, result.gameModeId)
        assertEquals(222, result.eventId)
        assertEquals(BattleType.RANKED_LEAGUE, result.battleType)
        assertEquals("#STAR999", result.starPlayerTag)
        assertEquals(null, result.teams)
        assertEquals(90.seconds, result.duration)
        assertEquals(participants, result.participants)
        assertEquals(BattleResult.DEFEAT, result.battleResult)
    }

    @Test
    fun `should handle empty participants and teams correctly`() {
        // GIVEN
        val json = Json
        val config = PlayerDatabaseConfig(playerTagMaxLength = 16, clubTagMaxLength = 16, playerNameMaxLength = 32)
        val table = PlayerBattleTable(config, json)
        val row = mockk<ResultRow>()

        val now = Clock.System.now()

        every { row[table.gameModeId] } returns 99
        every { row[table.eventId] } returns 888
        every { row[table.battleType] } returns BattleType.TROPHY_LEAGUE
        every { row[table.starPlayerTag] } returns "#STAR_EMPTY"
        every { row[table.teams] } returns emptyList()
        every { row[table.duration] } returns 30.seconds
        every { row[table.participants] } returns emptyList()
        every { row[table.battleResult] } returns BattleResult.DRAW
        every { row[table.battleTime] } returns now.minus(30.seconds)
        every { row[table.fetchTime] } returns now

        // WHEN
        val result = row.toDbPlayerBattle(table)

        // THEN
        assertEquals(99, result.gameModeId)
        assertEquals(888, result.eventId)
        assertEquals(BattleType.TROPHY_LEAGUE, result.battleType)
        assertEquals("#STAR_EMPTY", result.starPlayerTag)
        assertEquals(emptyList(), result.teams)
        assertEquals(30.seconds, result.duration)
        assertEquals(emptyList(), result.participants)
        assertEquals(BattleResult.DRAW, result.battleResult)
    }

    @Test
    fun `should map all fields correctly for player battle snapshot`() {
        // GIVEN
        val json = Json
        val config = PlayerDatabaseConfig(playerTagMaxLength = 16, clubTagMaxLength = 16, playerNameMaxLength = 32)
        val table = PlayerBattleSnapshotsTable(json, config)
        val row = mockk<ResultRow>()
        val now = Clock.System.now()
        val responseJson = buildJsonObject { put("battle", JsonPrimitive("data")) }

        every { row[table.playerTag] } returns "#PLAYER123"
        every { row[table.gameModeId] } returns 42
        every { row[table.battleResult] } returns BattleResult.VICTORY
        every { row[table.responseJson] } returns responseJson
        every { row[table.battleTime] } returns now.minus(60.seconds)
        every { row[table.fetchTime] } returns now

        // WHEN
        val result = row.toDbPlayerBattleSnapshot(table)

        // THEN
        assertEquals("#PLAYER123", result.playerTag)
        assertEquals(42, result.gameModeId)
        assertEquals(BattleResult.VICTORY, result.battleResult)
        assertEquals(responseJson, result.responseJson)
        assertEquals(now.minus(60.seconds), result.battleTime)
        assertEquals(now, result.fetchTime)
    }
}
