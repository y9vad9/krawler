package krawler.server

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import krawler.cli.parseArguments
import krawler.environment.SystemEnvironment

suspend fun main(args: Array<String>) {
    val server = Server(
        configuration = ServerConfiguration(
            environment = SystemEnvironment,
            arguments = args.parseArguments(),
        ),
        coroutineScope = CoroutineScope(Dispatchers.IO),
    )

    Runtime.getRuntime().addShutdownHook(Thread {
        server.stop(immediately = true)
    })

    server.start()
    server.awaitTermination()
}
