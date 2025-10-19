package krawler.telegram.bot

import krawler.cli.Arguments
import krawler.environment.Environment
import krawler.environment.getOrNull

/**
 * Configuration for external services and API keys.
 *
 * Resolves values from command-line arguments or environment variables.
 * Throws an error if required keys are not provided.
 */
class TelegramBotConfiguration(
    private val environment: Environment,
    private val arguments: Arguments,
) {
    private companion object {
        const val TELEGRAM_BOT_API_KEY_ENV = "TELEGRAM_BOT_API_KEY"
        const val TELEGRAM_BOT_API_KEY_ARG = "telegram-bot-api-key"

        const val KRAWLER_API_KEY_ENV = "KRAWLER_API_KEY"
        const val KRAWLER_API_KEY_ARG = "krawler-api-key"
    }

    /** Telegram bot API key, resolved from CLI argument or environment variable. */
    val telegramBotApiKey: String
        get() = arguments.getNamedOrNull(TELEGRAM_BOT_API_KEY_ARG)
            ?: environment.getOrNull(TELEGRAM_BOT_API_KEY_ENV)
            ?: error(
                "Telegram bot API key is not specified. Provide either " +
                        "command-line argument '--$TELEGRAM_BOT_API_KEY_ARG' " +
                        "or environment variable '$TELEGRAM_BOT_API_KEY_ENV'."
            )

    /** Krawler API key, resolved from CLI argument or environment variable. */
    val krawlerApiKey: String
        get() = arguments.getNamedOrNull(KRAWLER_API_KEY_ARG)
            ?: environment.getOrNull(KRAWLER_API_KEY_ENV)
            ?: error(
                "Krawler API key is not specified. Provide either " +
                        "command-line argument '--$KRAWLER_API_KEY_ARG' " +
                        "or environment variable '$KRAWLER_API_KEY_ENV'."
            )
}
