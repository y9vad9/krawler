package com.y9vad9.bcm.app

import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.file
import com.y9vad9.bcm.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.telegramBot
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import java.io.File
import java.util.*

class MainCommand : SuspendingCliktCommand("bcm.jar") {
    val configFile by option(
        names = arrayOf("config-file"),
        help = "Config file with list of Brawl Stars Clubs that are used in bot, and their settings. In a JSON format. If does not exist, will be created and filled up with default settings automatically.",
        envvar = "BCM_CONFIG_FILE",
    ).file(
        mustBeReadable = true,
        mustBeWritable = true,
    ).default(File(System.getProperty("user.dir"), "bcm-config.json"))

    val telegramApiKey by option(
        names = arrayOf("telegram-api-key"),
        help = "Telegram Bot API Key. The IP where the bot will be running should be in the allowed list of API key.",
    ).required()

    val brawlStarsApiKey by option(
        names = arrayOf("bs-api-key"),
        help = "Brawl Stars API Key. The IP where the bot will be running should be in the allowed list of API key.",
    ).required()

    val databaseUrl by option(
        names = arrayOf("database-url"),
        help = "Database URL. If not specified, it will run in-memory H2 database.",
    ).default("jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;")

    val databaseUser by option(
        names = arrayOf("database-user"),
        help = "Database User. If not specified, it will use 'root' as a user.",
    ).default("root")

    val databasePassword by option(
        names = arrayOf("database-user"),
        help = "Database User's Password.",
    ).default("")

    val botLocale by option(
        names = arrayOf("bot-locale"),
        help = "Database User's Password.",
    ).choice("en", "uk", "ru").default("en")

    override suspend fun run() {
        val database = Database.connect(
            url = databaseUrl,
            user = databaseUser,
            password = databasePassword,
        )

        val appDependencies = AppDependencies(
            configFile = configFile.toPath(),
            database = database,
            json = Json {
                ignoreUnknownKeys = true
            },
            telegramBot(telegramApiKey),
            locale = Locale.of(botLocale),
            bsApiKey = brawlStarsApiKey,
        )

        TelegramBot.start(appDependencies)
    }
}