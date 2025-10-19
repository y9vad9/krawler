package krawler.server

import krawler.cli.Arguments
import krawler.environment.Environment
import krawler.environment.getOrNull

/**
 * Server configuration.
 *
 * If the `--dev` argument is present, default values are applied for configuration properties.
 */
class ServerConfiguration(
    private val environment: Environment,
    private val arguments: Arguments,
) {

    companion object {
        private const val ARG_GRPC_SERVER_PORT = "grpc-server-port"
        private const val ENV_GRPC_SERVER_PORT = "GRPC_SERVER_PORT"
        private const val DEV_DEFAULT_PORT = 8181
    }

    /** gRPC server port, falling back to [DEV_DEFAULT_PORT] when `--dev` mode is active. */
    val grpcServerPort: Int
        get() = when {
            arguments.isPresent(ARG_GRPC_SERVER_PORT) -> {
                val value = arguments.getNamedOrNull(ARG_GRPC_SERVER_PORT)
                value?.toIntOrNull() ?: error(
                    "Invalid value for '--$ARG_GRPC_SERVER_PORT': expected a number, got '$value'."
                )
            }

            environment.getOrNull(ENV_GRPC_SERVER_PORT) != null -> {
                val value = environment.getOrNull(ENV_GRPC_SERVER_PORT)
                value?.toIntOrNull() ?: error(
                    "Invalid value for environment variable '$ENV_GRPC_SERVER_PORT': expected a number, got '$value'."
                )
            }

            arguments.isDevMode -> { DEV_DEFAULT_PORT }

            else -> {
                error(
                    "gRPC server port is not specified. Provide either " +
                            "command-line argument '--$ARG_GRPC_SERVER_PORT', environment variable " +
                            "'$ENV_GRPC_SERVER_PORT', or run with '--dev'."
                )
            }
        }
}

private val Arguments.isDevMode: Boolean get() = isPresent("dev")
