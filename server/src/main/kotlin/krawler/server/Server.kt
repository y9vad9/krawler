package krawler.server

import io.grpc.ServerBuilder
import io.grpc.ServerServiceDefinition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import io.grpc.Server as GrpcServer

/**
 * Simple gRPC server wrapper that starts and stops a server on a configurable port.
 *
 * @param configuration Server configuration retrieved from CLI arguments / environment.
 * @param coroutineScope Coroutine scope used to launch server.
 */
class Server(
    private val configuration: ServerConfiguration,
    private val coroutineScope: CoroutineScope,
) {
    private val koinApp: KoinApplication = KoinApplication.init()
    private val koin: Koin get() = koinApp.koin

    private val grpcServer: Deferred<GrpcServer> = coroutineScope.async(start = CoroutineStart.LAZY) {
        ServerBuilder.forPort(configuration.grpcServerPort)
            .addServices(koin.getAll<ServerServiceDefinition>())
            .build()
            .start()
    }

    /** Adds a new koin module */
    fun addKoinModule(module: Module) {
        koinApp.modules(module)
    }

    /** Starts the gRPC server */
    suspend fun start() {
        grpcServer.await()
    }

    /** Suspends until it terminates */
    suspend fun awaitTermination() {
        grpcServer.await().awaitTermination()
    }

    /** Stops the gRPC server. If [immediately] is true, shuts down immediately. */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun stop(immediately: Boolean = false) {
        if (!grpcServer.isCompleted) error("gRPC server was not started")

        val server = grpcServer.getCompleted()
        if (immediately) server.shutdownNow() else server.shutdown()
    }
}
