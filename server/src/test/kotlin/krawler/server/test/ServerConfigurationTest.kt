package krawler.server.test

import krawler.cli.parseArguments
import krawler.environment.InMemoryEnvironment
import krawler.server.ServerConfiguration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ServerConfigurationTest {

    @Test
    fun `returns port from arguments when valid`() {
        // GIVEN
        val environment = InMemoryEnvironment()
        val arguments = arrayOf("--grpc-server-port=9000").parseArguments()

        // WHEN
        val configuration = ServerConfiguration(
            environment = environment,
            arguments = arguments,
        )

        // THEN
        assertEquals(expected = 9000, actual = configuration.grpcServerPort)
    }

    @Test
    fun `throws when argument port is invalid number`() {
        // GIVEN
        val environment = InMemoryEnvironment()
        val arguments = arrayOf("--grpc-server-port=not-a-number").parseArguments()

        // WHEN // THEN
        val exception = assertFailsWith<IllegalStateException> {
            ServerConfiguration(
                environment = environment,
                arguments = arguments,
            ).grpcServerPort
        }

        assertEquals(
            expected = "Invalid value for '--grpc-server-port': expected a number, got 'not-a-number'.",
            actual = exception.message,
        )
    }

    @Test
    fun `returns port from environment when valid`() {
        // GIVEN
        val environment = InMemoryEnvironment(mapOf("GRPC_SERVER_PORT" to "7000"))
        val arguments = emptyArray<String>().parseArguments()

        // WHEN
        val configuration = ServerConfiguration(
            environment = environment,
            arguments = arguments,
        )

        // THEN
        assertEquals(expected = 7000, actual = configuration.grpcServerPort)
    }

    @Test
    fun `throws when environment port is invalid number`() {
        // GIVEN
        val environment = InMemoryEnvironment(mapOf("GRPC_SERVER_PORT" to "oops"))
        val arguments = emptyArray<String>().parseArguments()

        // WHEN & THEN
        val exception = assertFailsWith<IllegalStateException> {
            ServerConfiguration(
                environment = environment,
                arguments = arguments,
            ).grpcServerPort
        }

        assertEquals(
            expected = "Invalid value for environment variable 'GRPC_SERVER_PORT': expected a number, got 'oops'.",
            actual = exception.message,
        )
    }

    @Test
    fun `returns default port in dev mode`() {
        // GIVEN
        val environment = InMemoryEnvironment()
        val arguments = arrayOf("--dev").parseArguments()

        // WHEN
        val configuration = ServerConfiguration(
            environment = environment,
            arguments = arguments,
        )

        // THEN
        assertEquals(expected = 8181, actual = configuration.grpcServerPort)
    }

    @Test
    fun `throws when port is missing and not in dev mode`() {
        // GIVEN
        val environment = InMemoryEnvironment()
        val arguments = emptyArray<String>().parseArguments()

        // WHEN & THEN
        val exception = assertFailsWith<IllegalStateException> {
            ServerConfiguration(
                environment = environment,
                arguments = arguments,
            ).grpcServerPort
        }

        assertEquals(
            expected = "gRPC server port is not specified. Provide either " +
                    "command-line argument '--grpc-server-port', environment variable " +
                    "'GRPC_SERVER_PORT', or run with '--dev'.",
            actual = exception.message,
        )
    }
}
