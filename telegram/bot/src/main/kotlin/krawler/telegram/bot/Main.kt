package krawler.telegram.bot

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import krawler.cli.parseArguments
import krawler.environment.SystemEnvironment

suspend fun main(args: Array<String>) {
    val bot = TelegramBot(
        configuration = TelegramBotConfiguration(
            environment = SystemEnvironment,
            arguments = args.parseArguments(),
        ),
        coroutineScope = CoroutineScope(Dispatchers.IO),
    )

    Runtime.getRuntime().addShutdownHook(Thread {
        bot.stop()
    })
    bot.start().awaitTermination()
}
