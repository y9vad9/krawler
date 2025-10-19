package krawler.telegram.bot

import dev.inmo.tgbotapi.extensions.behaviour_builder.telegramBotWithBehaviourAndLongPolling
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async

/**
 * Telegram bot wrapper that manages the bot’s lifecycle.
 *
 * Handles startup, long polling, and graceful shutdown using coroutines.
 */
class TelegramBot(
    private val configuration: TelegramBotConfiguration,
    private val coroutineScope: CoroutineScope,
) {
    private val botDeferred = coroutineScope.async(start = CoroutineStart.LAZY) {
        telegramBotWithBehaviourAndLongPolling(
            token = configuration.telegramBotApiKey,
            scope = coroutineScope,
        ) {}.second
    }

    /** Starts the Telegram bot asynchronously. */
    suspend fun start(): TelegramBot {
        botDeferred.await()
        return this
    }

    /** Waits until the bot finishes execution or is stopped. */
    suspend fun awaitTermination(): TelegramBot {
        botDeferred.await().join()
        return this
    }

    /** Stops the Telegram bot if it is running. */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun stop() {
        if (!botDeferred.isActive) return
        if (botDeferred.isCompleted) botDeferred.getCompleted().cancel()

        botDeferred.cancel()
    }
}
