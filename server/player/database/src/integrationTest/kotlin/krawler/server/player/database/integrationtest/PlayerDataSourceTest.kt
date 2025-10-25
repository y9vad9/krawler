package krawler.server.player.database.integrationtest

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import io.r2dbc.postgresql.codec.EnumCodec
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import krawler.exposed.createEnumTypeIgnoring
import krawler.exposed.suspendWriteTransaction
import krawler.server.player.database.BattleResult
import krawler.server.player.database.BattleType
import krawler.server.player.database.DbPlayer
import krawler.server.player.database.DbPlayerBattle
import krawler.server.player.database.DbPlayerBrawler
import krawler.server.player.database.PlayerBattleTable
import krawler.server.player.database.PlayerBrawlerTable
import krawler.server.player.database.PlayerDataSource
import krawler.server.player.database.PlayerDatabaseConfig
import krawler.server.player.database.PlayerTable
import krawler.server.player.database.TeamPlayer
import org.jetbrains.exposed.v1.core.vendors.PostgreSQLDialect
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabaseConfig
import org.jetbrains.exposed.v1.r2dbc.SchemaUtils
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PlayerDataSourceTest {

    private val postgres = PostgreSQLContainer<Nothing>(DockerImageName.parse("postgres:16.1")).apply {
        withDatabaseName("testdb")
        withUsername("test")
        withPassword("test")
        waitingFor(Wait.forListeningPort())
    }

    private lateinit var database: R2dbcDatabase
    private lateinit var dataSource: PlayerDataSource

    private val json = Json
    private val config = PlayerDatabaseConfig(
        playerTagMaxLength = 20,
        playerNameMaxLength = 20,
        clubTagMaxLength = 20,
    )

    @BeforeAll
    fun setupContainerAndDb() {
        postgres.start()

        val options = PostgresqlConnectionConfiguration.builder()
            .host(postgres.host)
            .port(postgres.firstMappedPort)
            .username(postgres.username)
            .password(postgres.password)
            .database(postgres.databaseName)
            // registered SQL enum name must match final name in database (PG always lower-case wrapped)
            .codecRegistrar(
                EnumCodec.builder()
                    .withEnum("battleresult", BattleResult::class.java)
                    .withEnum("battletype", BattleType::class.java)
                    .build()
            )
            .build()

        val cxFactory = PostgresqlConnectionFactory(options)

        database = R2dbcDatabase.connect(
            connectionFactory = cxFactory,
            databaseConfig = R2dbcDatabaseConfig {
                explicitDialect = PostgreSQLDialect()
            },
        )

        dataSource = PlayerDataSource(
            database = database,
            config = config,
            json = json,
        )
    }

    @BeforeAll
    fun createSchema() = runTest {
        suspendWriteTransaction(database) {
            createEnumTypeIgnoring<BattleType>()
            createEnumTypeIgnoring<BattleResult>()
            SchemaUtils.create(
                PlayerTable(config),
                PlayerBrawlerTable(config),
                PlayerBattleTable(config, json)
            )
        }
    }

    @AfterAll
    fun teardown() {
        postgres.stop()
    }

    @Test
    fun `addOrSetPlayer and getPlayer should persist and retrieve player`() = runTest {
        // GIVEN
        val now = Instant.fromEpochSeconds(1_700_000_000)
        val player = DbPlayer(
            tag = "#AAA",
            clubTag = "#CLUB",
            name = "Spike",
            nameColor = "0xffffff",
            currentTrophies = 1000,
            highestTrophies = 1500,
            currentRankedStage = 2,
            highestRankedStage = 3,
            soloVictories = 10,
            duoVictories = 5,
            trioVictories = 1,
            quintetVictories = 0,
            bestRoboRumbleTime = 30.seconds,
            bestTimeAsBigBrawler = 25.seconds,
            expLevel = 9,
            expPoints = 1200,
            brawlersAmount = 10,
            currentBestBrawlerId = 4,
            currentFavoriteBrawlerId = 2,
            actualizedAt = now
        )

        // WHEN
        dataSource.addOrSetPlayer(player)
        val found = dataSource.getPlayer(tag = "#AAA")

        // THEN
        assertNotNull(found)
        assertEquals(expected = player.tag, actual = found.tag)
        assertEquals(expected = player.name, actual = found.name)
    }

    @Test
    fun `getPlayer should filter by actualization time`() = runTest {
        // GIVEN
        val now = Instant.fromEpochSeconds(1_700_000_000)
        val player = DbPlayer(
            tag = "#BBB",
            clubTag = null,
            name = "Colt",
            nameColor = "0xff0000",
            currentTrophies = 500,
            highestTrophies = 600,
            currentRankedStage = 1,
            highestRankedStage = 2,
            soloVictories = 2,
            duoVictories = 2,
            trioVictories = 0,
            quintetVictories = 0,
            bestRoboRumbleTime = null,
            bestTimeAsBigBrawler = null,
            expLevel = 5,
            expPoints = 200,
            brawlersAmount = 3,
            currentBestBrawlerId = 1,
            currentFavoriteBrawlerId = 1,
            actualizedAt = now
        )

        dataSource.addOrSetPlayer(player)

        // WHEN
        val outdated = dataSource.getPlayer(tag = "#BBB", afterActualization = now + 10.seconds)
        val fresh = dataSource.getPlayer(tag = "#BBB", afterActualization = now - 10.seconds)

        // THEN
        assertNull(outdated)
        assertNotNull(fresh)
    }

    @Test
    fun `addOrSetPlayers should upsert multiple players`() = runTest {
        // GIVEN
        val now = Instant.fromEpochSeconds(1_700_000_000)
        val players = listOf(
            DbPlayer(
                tag = "#C1",
                clubTag = null,
                name = "Shelly",
                nameColor = "0x00ff00",
                currentTrophies = 800,
                highestTrophies = 900,
                currentRankedStage = 1,
                highestRankedStage = 1,
                soloVictories = 4,
                duoVictories = 2,
                trioVictories = 1,
                quintetVictories = 0,
                bestRoboRumbleTime = 1.seconds,
                bestTimeAsBigBrawler = 1.seconds,
                expLevel = 7,
                expPoints = 600,
                brawlersAmount = 5,
                currentBestBrawlerId = 2,
                currentFavoriteBrawlerId = 2,
                actualizedAt = now
            ),
            DbPlayer(
                tag = "#C2",
                clubTag = "#CC",
                name = "Piper",
                nameColor = "0x0000ff",
                currentTrophies = 700,
                highestTrophies = 1000,
                currentRankedStage = 2,
                highestRankedStage = 2,
                soloVictories = 10,
                duoVictories = 4,
                trioVictories = 3,
                quintetVictories = 1,
                bestRoboRumbleTime = 60.seconds,
                bestTimeAsBigBrawler = 45.seconds,
                expLevel = 8,
                expPoints = 800,
                brawlersAmount = 7,
                currentBestBrawlerId = 3,
                currentFavoriteBrawlerId = 4,
                actualizedAt = now
            )
        )

        // WHEN
        dataSource.addOrSetPlayers(players)

        // THEN
        val found1 = dataSource.getPlayer(tag = "#C1")
        val found2 = dataSource.getPlayer(tag = "#C2")

        assertNotNull(found1)
        assertNotNull(found2)
        assertEquals(expected = "Shelly", actual = found1.name)
        assertEquals(expected = "Piper", actual = found2.name)
    }

    @Test
    fun `addOrSetPlayerBrawlers with nulls and non-nulls`() = runTest {
        // GIVEN
        val brawlers = listOf(
            DbPlayerBrawler(
                playerTag = "#PLAYER_NULLS",
                brawlerId = 1,
                powerLevel = 5,
                rank = 10,
                trophies = 200,
                highestTrophies = 250,
                gears = emptyList(),
                starPowers = emptyList(),
                gadgets = emptyList(),
                currentWinStreak = null,
                highestWinStreak = null
            ),
            DbPlayerBrawler(
                playerTag = "#PLAYER_NONNULL",
                brawlerId = 2,
                powerLevel = 6,
                rank = 12,
                trophies = 300,
                highestTrophies = 350,
                gears = listOf(1, 2),
                starPowers = listOf(1),
                gadgets = listOf(2),
                currentWinStreak = 3,
                highestWinStreak = 5
            )
        )

        // WHEN
        dataSource.addOrSetPlayerBrawlers(brawlers)
        val retrievedNulls = dataSource.getPlayerBrawlers("#PLAYER_NULLS")
        val retrievedNonNull = dataSource.getPlayerBrawlers("#PLAYER_NONNULL")

        // THEN
        assertEquals(1, retrievedNulls.size)
        val nullBrawler = retrievedNulls.first()
        assertNull(nullBrawler.currentWinStreak)
        assertNull(nullBrawler.highestWinStreak)
        assertTrue(nullBrawler.gears.isEmpty())

        val nonNullBrawler = retrievedNonNull.first()
        assertEquals(3, nonNullBrawler.currentWinStreak)
        assertEquals(listOf(1, 2), nonNullBrawler.gears)
        assertEquals(listOf(1), nonNullBrawler.starPowers)
    }

    @Test
    fun `addOrIgnorePlayerBattles with mixed data`() = runTest {
        // GIVEN
        val now = Clock.System.now()
        val battles = listOf(
            // battle with all optional fields null
            DbPlayerBattle(
                gameModeId = 1,
                eventId = 1,
                battleType = BattleType.TROPHY_LEAGUE,
                starPlayerTag = "#PLAYER_NULL",
                teams = null,
                duration = 60.seconds,
                participants = listOf(
                    TeamPlayer(playerTag = "#PLAYER_NULL", name = "Colt", power = 5, brawler = TeamPlayer.Brawler(1, 5))
                ),
                battleResult = BattleResult.DEFEAT,
                battleTime = now,
                fetchTime = now
            ),
            // battle with all optional fields non-null
            DbPlayerBattle(
                gameModeId = 2,
                eventId = 2,
                battleType = BattleType.RANKED_LEAGUE,
                starPlayerTag = "#PLAYER_FULL",
                teams = listOf(listOf("p1", "p2"), listOf("p3")),
                duration = 120.seconds,
                participants = listOf(
                    TeamPlayer(
                        playerTag = "#PLAYER_FULL",
                        name = "Spike",
                        power = 10,
                        brawler = TeamPlayer.Brawler(2, 10, trophies = 50, rankedStage = 1),
                    )
                ),
                battleResult = BattleResult.VICTORY,
                battleTime = now + 1.seconds,
                fetchTime = now + 1.seconds
            )
        )

        // WHEN
        dataSource.addOrIgnorePlayerBattles(battles)

        val retrievedNull = dataSource.getPlayerBattles("#PLAYER_NULL", now..(now + 10.seconds))
        val retrievedFull = dataSource.getPlayerBattles("#PLAYER_FULL", now..(now + 10.seconds))

        // THEN
        assertEquals(1, retrievedNull.size)
        val nullBattle = retrievedNull.first()
        assertNull(nullBattle.teams)
        assertEquals(1, nullBattle.participants.size)

        assertEquals(1, retrievedFull.size)
        val fullBattle = retrievedFull.first()
        assertNotNull(fullBattle.teams)
        assertEquals(1, fullBattle.participants.size)
        assertEquals(50, fullBattle.participants.first().brawler.trophies)
        assertEquals(1, fullBattle.participants.first().brawler.rankedStage)
    }

    @Test
    fun `addOrIgnorePlayerBattles ignores duplicates`() = runTest {
        // GIVEN
        val now = Clock.System.now()
        val battle = DbPlayerBattle(
            gameModeId = 1,
            eventId = 1,
            battleType = BattleType.TROPHY_LEAGUE,
            starPlayerTag = "#PLAYER_DUP",
            teams = null,
            duration = 60.seconds,
            participants = listOf(
                TeamPlayer(playerTag = "#PLAYER_DUP", name = "Colt", power = 5, brawler = TeamPlayer.Brawler(1, 5))
            ),
            battleResult = BattleResult.DEFEAT,
            battleTime = now,
            fetchTime = now
        )

        // WHEN
        dataSource.addOrIgnorePlayerBattles(listOf(battle, battle))
        val retrieved = dataSource.getPlayerBattles("#PLAYER_DUP", now..(now + 1.seconds))

        // THEN
        assertEquals(1, retrieved.size) // duplicates ignored
    }

    @Test
    fun `getPlayerBrawlers returns empty list when no data`() = runTest {
        val retrieved = dataSource.getPlayerBrawlers("#UNKNOWN")
        assertTrue(retrieved.isEmpty())
    }

    @Test
    fun `getPlayerBattles returns empty list when no data`() = runTest {
        val now = Clock.System.now()
        val retrieved = dataSource.getPlayerBattles("#UNKNOWN", now..(now + 10.seconds))
        assertTrue(retrieved.isEmpty())
    }
}
