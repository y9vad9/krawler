package com.y9vad9.starix.app

import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.file
import com.y9vad9.starix.bot.TelegramBot
import com.y9vad9.starix.core.system.entity.Settings
import dev.inmo.tgbotapi.extensions.api.bot.getMe
import dev.inmo.tgbotapi.extensions.api.telegramBot
import dev.inmo.tgbotapi.types.message.textsources.pre
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import java.io.File
import java.util.*

class MainCommand : SuspendingCliktCommand("bcm.jar") {
    val configFile by option(
        names = arrayOf("--config_file"),
        help = "Config file with list of Brawl Stars Clubs that are used in bot, and their settings. In a JSON format. If does not exist, will be created and filled up with default settings automatically.",
        envvar = "BCM_CONFIG_FILE",
    ).file(
        mustBeReadable = true,
        mustBeWritable = true,
    ).default(File(System.getProperty("user.dir"), "bcm-config.json"))

    val telegramApiKey by option(
        names = arrayOf("--telegram_api_key"),
        envvar = "TELEGRAM_API_KEY",
        help = "Telegram Bot API Key. The IP where the bot will be running should be in the allowed list of API key.",
    ).required()

    val brawlStarsApiKey by option(
        names = arrayOf("--bs_api_key"),
        envvar = "BRAWL_STARS_API_KEY",
        help = "Brawl Stars API Key. The IP where the bot will be running should be in the allowed list of API key.",
    ).required()

    val databaseUrl by option(
        names = arrayOf("--database_url"),
        envvar = "DB_URL",
        help = "Database URL. If not specified, it will run in-memory H2 database.",
    ).required()

    val databaseUser by option(
        names = arrayOf("--database_user"),
        envvar = "DB_USER",
        help = "Database User. If not specified, it will use 'root' as a user.",
    ).default("root")

    val databasePassword by option(
        names = arrayOf("--database_password"),
        envvar = "DB_PASSWORD",
        help = "Database User's Password.",
    ).default("")

    val botLocale by option(
        names = arrayOf("--bot_locale"),
        envvar = "BOT_LOCALE",
        help = "Database User's Password.",
    ).choice("en", "uk", "ru").default("en")

    override suspend fun run() {
        if (!configFile.exists()) {
            val json = Json {
                encodeDefaults = true
                ignoreUnknownKeys = true
            }

            configFile.createNewFile()
            configFile.writeText(json.encodeToString(Settings()))
            echo("Config file was not specified and the default one was not present. The default config is created at ${configFile.absoluteFile}.")
            echo("Once you finished with setting it up, run the program again.")
            return
        }

        val database = Database.connect(
            url = databaseUrl,
            user = databaseUser,
            password = databasePassword,
        )

        val telegramBot = telegramBot(
            token = telegramApiKey,
            clientConfig = {
                install(Logging) {
                    level = LogLevel.ALL
                    logger = Logger.DEFAULT
                }
            }
        )

        val appDependencies = AppDependencies(
            configFile = configFile.toPath(),
            database = database,
            json = Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            },
            bot = telegramBot,
            locale = Locale.of(botLocale),
            bsApiKey = brawlStarsApiKey,
            tgBotTag = telegramBot.getMe().username!!.username,
        )

        TelegramBot.start(appDependencies)
    }
}