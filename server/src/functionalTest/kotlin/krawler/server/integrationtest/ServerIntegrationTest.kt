package krawler.server.integrationtest

import io.grpc.ServerServiceDefinition
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import krawler.cli.parseArguments
import krawler.environment.InMemoryEnvironment
import krawler.server.Server
import krawler.server.ServerConfiguration
import org.junit.jupiter.api.Test
import org.koin.core.module.Module
import org.koin.dsl.module
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class ServerTest {

    private fun createTestConfiguration(port: Int = 50051): ServerConfiguration {
        return ServerConfiguration(
            environment = InMemoryEnvironment(emptyMap()),
            arguments = arrayOf("--grpc-server-port=$port").parseArguments(),
        )
    }

    private fun createTestService(): ServerServiceDefinition {
        // A reflection service is sufficient for testing service registration
        return ServerServiceDefinition.builder("IntegrationTestService")
            .build()
    }

    @Test
    fun `server starts and stops successfully`() = runTest(timeout = 5.seconds) {
        // GIVEN
        val configuration = createTestConfiguration()
        val testScope = TestScope(StandardTestDispatcher(testScheduler))
        val server = Server(configuration = configuration, coroutineScope = testScope)

        // Register dummy gRPC service through Koin
        val testModule: Module = module {
            single<ServerServiceDefinition> { createTestService() }
        }
        server.addKoinModule(testModule)

        // THEN
        server.start()
        server.stop()
    }

    @Test
    fun `stop throws when server not started`() = runTest {
        // GIVEN
        val configuration = createTestConfiguration()
        val testScope = TestScope(StandardTestDispatcher(testScheduler))
        val server = Server(configuration = configuration, coroutineScope = testScope)

        // WHEN / THEN
        assertFailsWith<IllegalStateException> {
            server.stop()
        }
    }
}
