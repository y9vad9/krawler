package krawler.telegram.bot.test

import krawler.cli.parseArguments
import krawler.environment.InMemoryEnvironment
import krawler.telegram.bot.TelegramBotConfiguration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TelegramBotConfigurationTest {

    @Test
    fun `should resolve Telegram bot API key from CLI arguments`() {
        // GIVEN
        val args = arrayOf("--telegram-bot-api-key=cli-key").parseArguments()
        val env = InMemoryEnvironment(emptyMap())

        // WHEN
        val configuration = TelegramBotConfiguration(environment = env, arguments = args)

        // THEN
        assertEquals("cli-key", configuration.telegramBotApiKey)
    }

    @Test
    fun `should resolve Telegram bot API key from environment variable`() {
        // GIVEN
        val args = emptyArray<String>().parseArguments()
        val env = InMemoryEnvironment(mapOf("TELEGRAM_BOT_API_KEY" to "env-key"))

        // WHEN
        val configuration = TelegramBotConfiguration(environment = env, arguments = args)

        // THEN
        assertEquals("env-key", configuration.telegramBotApiKey)
    }

    @Test
    fun `should throw error if Telegram bot API key is not provided`() {
        // GIVEN
        val args = emptyArray<String>().parseArguments()
        val env = InMemoryEnvironment(emptyMap())

        // WHEN / THEN
        assertFailsWith<IllegalStateException> {
            TelegramBotConfiguration(environment = env, arguments = args).telegramBotApiKey
        }
    }

    @Test
    fun `should resolve Krawler API key from CLI arguments`() {
        // GIVEN
        val args = arrayOf("--krawler-api-key=cli-krawler").parseArguments()
        val env = InMemoryEnvironment(emptyMap())

        // WHEN
        val configuration = TelegramBotConfiguration(environment = env, arguments = args)

        // THEN
        assertEquals("cli-krawler", configuration.krawlerApiKey)
    }

    @Test
    fun `should resolve Krawler API key from environment variable`() {
        // GIVEN
        val args = emptyArray<String>().parseArguments()
        val env = InMemoryEnvironment(mapOf("KRAWLER_API_KEY" to "env-krawler"))

        // WHEN
        val configuration = TelegramBotConfiguration(environment = env, arguments = args)

        // THEN
        assertEquals("env-krawler", configuration.krawlerApiKey)
    }

    @Test
    fun `should throw error if Krawler API key is not provided`() {
        // GIVEN
        val args = emptyArray<String>().parseArguments()
        val env = InMemoryEnvironment(emptyMap())

        // WHEN / THEN
        assertFailsWith<IllegalStateException> {
            TelegramBotConfiguration(environment = env, arguments = args).krawlerApiKey
        }
    }
}
