package krawler.server.player.database.integrationtest

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import io.r2dbc.postgresql.codec.EnumCodec
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import krawler.exposed.createEnumTypeIgnoring
import krawler.exposed.suspendWriteTransaction
import krawler.server.player.database.BattleResult
import krawler.server.player.database.BattleType
import krawler.server.player.database.DbPlayerBattleSnapshot
import krawler.server.player.database.DbPlayerSnapshot
import krawler.server.player.database.PlayerBattleSnapshotsTable
import krawler.server.player.database.PlayerBattleTable
import krawler.server.player.database.PlayerBrawlerTable
import krawler.server.player.database.PlayerDatabaseConfig
import krawler.server.player.database.PlayerSnapshotsDataSource
import krawler.server.player.database.PlayerSnapshotsTable
import krawler.server.player.database.PlayerTable
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
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PlayerSnapshotsDataSourceTest {
    private val postgres = PostgreSQLContainer<Nothing>(DockerImageName.parse("postgres:16.1")).apply {
        withDatabaseName("testdb")
        withUsername("test")
        withPassword("test")
        waitingFor(Wait.forListeningPort())
    }

    private lateinit var database: R2dbcDatabase
    private lateinit var dataSource: PlayerSnapshotsDataSource

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

        dataSource = PlayerSnapshotsDataSource(
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
                PlayerSnapshotsTable(json, config),
                PlayerBattleSnapshotsTable(json, config)
            )
        }
    }

    @AfterAll
    fun teardown() {
        postgres.stop()
    }

    @Test
    fun `insert and retrieve single player snapshot`() = runTest {
        // GIVEN
        val now = Clock.System.now()
        val snapshot = DbPlayerSnapshot(
            tag = "#PLAYER_SINGLE",
            clubTag = "#CLUB_A",
            fetchTime = now,
            responseJson = buildJsonObject { put("trophies", JsonPrimitive(5000)) }
        )

        // WHEN
        dataSource.insertPlayerRecord(snapshot)
        val retrieved = dataSource.getLastPlayerRecord("#PLAYER_SINGLE")

        // THEN
        assertNotNull(retrieved)
        assertEquals("#PLAYER_SINGLE", retrieved!!.tag)
        assertEquals("#CLUB_A", retrieved.clubTag)
        assertEquals(5000, retrieved.responseJson["trophies"]?.jsonPrimitive?.int)
    }

    @Test
    fun `insert multiple player snapshots with null and non-null clubTag`() = runTest {
        val now = Clock.System.now()
        val snapshots = listOf(
            DbPlayerSnapshot(
                tag = "#PLAYER_NULL",
                clubTag = null,
                fetchTime = now,
                responseJson = buildJsonObject { put("test", JsonPrimitive(1)) }
            ),
            DbPlayerSnapshot(
                tag = "#PLAYER_NONNULL",
                clubTag = "#CLUB_X",
                fetchTime = now.plus(5.seconds),
                responseJson = buildJsonObject { put("test", JsonPrimitive(2)) }
            )
        )

        dataSource.insertPlayerRecords(snapshots)
        val resultNull = dataSource.getLastPlayerRecord("#PLAYER_NULL")
        val resultNonnull = dataSource.getLastPlayerRecord("#PLAYER_NONNULL")

        assertNotNull(resultNull)
        assertNull(resultNull.clubTag)
        assertEquals("#CLUB_X", resultNonnull!!.clubTag)
    }

    @Test
    fun `getPlayerRecords returns correct results within timeline`() = runTest {
        val start = Clock.System.now()
        val mid = start.plus(10.seconds)
        val end = start.plus(20.seconds)

        val snapshots = listOf(
            DbPlayerSnapshot("#PLAYER_TIME", "#CLUB1", buildJsonObject { put("s", JsonPrimitive(1)) }, start),
            DbPlayerSnapshot("#PLAYER_TIME", "#CLUB2", buildJsonObject { put("s", JsonPrimitive(2)) }, mid),
            DbPlayerSnapshot("#PLAYER_TIME", "#CLUB3", buildJsonObject { put("s", JsonPrimitive(3)) }, end),
        )
        dataSource.insertPlayerRecords(snapshots)

        val results = dataSource.getPlayerRecords("#PLAYER_TIME", (mid..end))!!

        assertEquals(2, results.size)
        assertEquals("#CLUB3", results.first().clubTag)
        assertEquals("#CLUB2", results.last().clubTag)
    }

    @Test
    fun `getPlayerRecords returns null when no data in range`() = runTest {
        val now = Clock.System.now()
        val result = dataSource.getPlayerRecords("#PLAYER_EMPTY", (now..now.plus(10.seconds)))
        assertNull(result)
    }

    @Test
    fun `insert and retrieve single battle snapshot`() = runTest {
        val now = Clock.System.now()
        val battle = DbPlayerBattleSnapshot(
            playerTag = "#BATTLE_SINGLE",
            battleResult = BattleResult.VICTORY,
            fetchTime = now,
            battleTime = now.minus(5.seconds),
            gameModeId = 42,
            responseJson = buildJsonObject { put("mode", JsonPrimitive("solo")) }
        )

        dataSource.insertPlayerBattleRecord(battle)
        val result = dataSource.getPlayerBattleRecords("#BATTLE_SINGLE", (now.minus(10.seconds)..now.plus(10.seconds)))

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals(expected = BattleResult.VICTORY, actual = result.first().battleResult)
        assertEquals(42, result.first().gameModeId)
        assertEquals("solo", result.first().responseJson["mode"]?.jsonPrimitive?.content)
    }

    @Test
    fun `insert multiple battle snapshots with nulls and duplicates ignored`() = runTest {
        // GIVEN
        val now = Clock.System.now()
        val battles = listOf(
            DbPlayerBattleSnapshot(
                playerTag = "#PLAYER_DUP",
                battleResult = BattleResult.DEFEAT,
                fetchTime = now,
                battleTime = now,
                gameModeId = 10,
                responseJson = buildJsonObject { put("r", JsonNull) }
            ),
            DbPlayerBattleSnapshot(
                playerTag = "#PLAYER_DUP",
                battleResult = BattleResult.VICTORY,
                fetchTime = now.plus(1.seconds),
                battleTime = now.plus(1.seconds),
                gameModeId = 11,
                responseJson = buildJsonObject { put("r", JsonPrimitive(BattleResult.VICTORY.name)) }
            ),
            // Duplicate should be ignored
            DbPlayerBattleSnapshot(
                playerTag = "#PLAYER_DUP",
                battleResult = BattleResult.VICTORY,
                fetchTime = now.plus(1.seconds),
                battleTime = now.plus(1.seconds),
                gameModeId = 11,
                responseJson = buildJsonObject { put("r", JsonPrimitive(BattleResult.VICTORY.name)) }
            )
        )

        // WHEN
        dataSource.insertPlayerBattleRecords(battles)
        val result = dataSource.getPlayerBattleRecords("#PLAYER_DUP", (now..now.plus(5.seconds)))

        // THEN
        assertNotNull(result)
        assertEquals(2, result.size)
        assertEquals(BattleResult.VICTORY, result.first().battleResult)
    }

    @Test
    fun `getPlayerBattleRecords returns null when none exist`() = runTest {
        val now = Clock.System.now()
        val result = dataSource.getPlayerBattleRecords("#UNKNOWN", (now..now.plus(10.seconds)))
        assertNull(result)
    }
}