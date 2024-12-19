package com.y9vad9.starix.app

import com.y9vad9.starix.foundation.time.SystemTimeProvider
import com.y9vad9.starix.bot.BotDependencies
import com.y9vad9.starix.core.brawlstars.usecase.CheckClubsAvailabilityUseCase
import com.y9vad9.starix.core.system.repository.SettingsRepository
import com.y9vad9.starix.core.system.usecase.CheckIsUserAllowedToJoinUseCase
import com.y9vad9.starix.core.system.usecase.GetAllowedClubsUseCase
import com.y9vad9.starix.core.system.usecase.GetLinkedPlayerInChatUseCase
import com.y9vad9.starix.core.system.usecase.GetNotLinkedBrawlStarsPlayersUseCase
import com.y9vad9.starix.core.system.usecase.GetUsersByTagUseCase
import com.y9vad9.starix.core.telegram.usecase.AddMemberToChatUseCase
import com.y9vad9.starix.core.telegram.usecase.LinkBrawlStarsPlayerUseCase
import com.y9vad9.starix.core.system.usecase.CheckUserStatusUseCase
import com.y9vad9.starix.data.BrawlStarsRepositoryImpl
import com.y9vad9.starix.data.ChatRepositoryImpl
import com.y9vad9.starix.data.SettingsRepositoryImpl
import com.y9vad9.starix.data.UserRepositoryImpl
import com.y9vad9.starix.data.brawlstars.BrawlStarsClient
import com.y9vad9.starix.data.database.UserBSAccountsTable
import com.y9vad9.starix.data.database.UsersTable
import com.y9vad9.starix.localization.EnglishStrings
import com.y9vad9.starix.localization.RussianStrings
import com.y9vad9.starix.localization.Strings
import com.y9vad9.starix.localization.UkrainianStrings
import dev.inmo.kslog.common.KSLog
import dev.inmo.tgbotapi.bot.TelegramBot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    tgBotTag: String,
) : BotDependencies {
    private val timeProvider = SystemTimeProvider(ZoneId.of("UTC"))
    private val brawlStarsRepository = com.y9vad9.starix.data.BrawlStarsRepositoryImpl(
        BrawlStarsClient(bsApiKey)
    )
    private val usersRepository = UserRepositoryImpl(
        usersTable = UsersTable(database),
        userBsAccounts = UserBSAccountsTable(database),
        bsRepository = brawlStarsRepository,
        timeProvider = timeProvider,
    )
    override val strings: Strings = when (locale.language) {
        "uk" -> UkrainianStrings(tgBotTag)
        "ru" -> RussianStrings(tgBotTag)
        else -> EnglishStrings(tgBotTag)
    }

    override val globalScope: CoroutineScope = CoroutineScope(Dispatchers.Default.limitedParallelism(6))

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
    override val checkIsUserAllowedToJoinUseCase: CheckIsUserAllowedToJoinUseCase = CheckIsUserAllowedToJoinUseCase(
        usersRepository, settingsRepository,
    )
    override val getLinkedPlayerInChatUseCase: GetLinkedPlayerInChatUseCase = GetLinkedPlayerInChatUseCase(
        usersRepository, settingsRepository,
    )
    override val getAllowedClubs: GetAllowedClubsUseCase = GetAllowedClubsUseCase(
        brawlStarsRepository, settingsRepository,
    )
    override val getNotLinkedBrawlStarsPlayers: GetNotLinkedBrawlStarsPlayersUseCase =
        GetNotLinkedBrawlStarsPlayersUseCase(
            brawlStarsRepository, usersRepository,
        )
    override val getUsersByTag: GetUsersByTagUseCase = GetUsersByTagUseCase(
        usersRepository
    )
}