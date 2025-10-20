package krawler.server

import kotlinx.coroutines.CoroutineScope
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.module.Module

/**
 * Simple gRPC server wrapper that starts and stops a server on a configurable port.
 *
 * @param configuration Server configuration retrieved from CLI arguments / environment.
 * @param coroutineScope Coroutine scope used to launch server.
 */
@Suppress("detekt.NotImplementedDeclaration", "detekt.UnusedParameter", "detekt.UnusedPrivateProperty")
class Server(
    private val configuration: ServerConfiguration,
    private val coroutineScope: CoroutineScope,
) {
    private val koinApp: KoinApplication = KoinApplication.init()
    private val koin: Koin get() = koinApp.koin

    /** Adds a new koin module */
    fun addKoinModule(module: Module) {
        koinApp.modules(module)
    }

    /** Starts the gRPC server */
    suspend fun start() {
        TODO()
    }

    /** Suspends until it terminates */
    suspend fun awaitTermination() {
        TODO()
    }

    /** Stops the gRPC server. If [immediately] is true, shuts down immediately. */
    fun stop(immediately: Boolean = false) {
        TODO()
    }
}
