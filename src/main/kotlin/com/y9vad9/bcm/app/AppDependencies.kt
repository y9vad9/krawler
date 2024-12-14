package com.y9vad9.bcm.app

import com.timemates.backend.time.SystemTimeProvider
import com.y9vad9.bcm.bot.BotDependencies
import com.y9vad9.bcm.core.brawlstars.usecase.CheckClubsAvailabilityUseCase
import com.y9vad9.bcm.core.system.repository.SettingsRepository
import com.y9vad9.bcm.core.telegram.usecase.AddMemberToChatUseCase
import com.y9vad9.bcm.core.telegram.usecase.LinkBrawlStarsPlayerUseCase
import com.y9vad9.bcm.core.user.usecase.CheckUserStatusUseCase
import com.y9vad9.bcm.core.user.usecase.GetAllowedClubsUseCase
import com.y9vad9.bcm.data.BrawlStarsRepositoryImpl
import com.y9vad9.bcm.data.ChatRepositoryImpl
import com.y9vad9.bcm.data.SettingsRepositoryImpl
import com.y9vad9.bcm.data.UserRepositoryImpl
import com.y9vad9.bcm.data.brawlstars.BrawlStarsClient
import com.y9vad9.bcm.data.database.UserBSAccountsTable
import com.y9vad9.bcm.data.database.UsersTable
import com.y9vad9.bcm.localization.EnglishStrings
import com.y9vad9.bcm.localization.RussianStrings
import com.y9vad9.bcm.localization.Strings
import com.y9vad9.bcm.localization.UkrainianStrings
import dev.inmo.kslog.common.KSLog
import dev.inmo.tgbotapi.bot.TelegramBot
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import java.nio.file.Path
import java.time.ZoneId
import java.util.*

class AppDependencies(
    configFile: Path,
    database: Database,
    json: Json,
    override val bot: TelegramBot,
    locale: Locale,
    bsApiKey: String,
) : BotDependencies {
    private val timeProvider = SystemTimeProvider(ZoneId.of("UTC"))
    private val brawlStarsRepository = BrawlStarsRepositoryImpl(
        BrawlStarsClient(bsApiKey)
    )
    private val usersRepository = UserRepositoryImpl(
        usersTable = UsersTable(database),
        userBsAccounts = UserBSAccountsTable(database),
        bsRepository = brawlStarsRepository,
        timeProvider = timeProvider,
    )
    override val strings: Strings = when (locale.language) {
        "uk" -> UkrainianStrings
        "ru" -> RussianStrings
        else -> EnglishStrings
    }

    private val chatRepository = ChatRepositoryImpl(bot, strings)

    override val settingsRepository: SettingsRepository = SettingsRepositoryImpl(configFile, json)
    override val logger: KSLog = KSLog("GlobalLogger")
    override val checkUserStatus: CheckUserStatusUseCase = CheckUserStatusUseCase(
        usersRepository, settingsRepository,
    )
    override val linkBrawlStarsPlayer: LinkBrawlStarsPlayerUseCase = LinkBrawlStarsPlayerUseCase(
        usersRepository, brawlStarsRepository, timeProvider
    )
    override val checkClubsAvailability: CheckClubsAvailabilityUseCase = CheckClubsAvailabilityUseCase(
        usersRepository, brawlStarsRepository, settingsRepository,
    )
    override val addMemberToChat: AddMemberToChatUseCase = AddMemberToChatUseCase(
        usersRepository, brawlStarsRepository, settingsRepository, chatRepository, timeProvider, checkClubsAvailability,
    )
    override val getAllowedClubs: GetAllowedClubsUseCase = GetAllowedClubsUseCase(
        settingsRepository, brawlStarsRepository,
    )
}