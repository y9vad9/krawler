@file:OptIn(ValidationDelicateApi::class)

package com.y9vad9.bcm.data

import com.y9vad9.bcm.domain.entity.ClubSettings
import com.y9vad9.bcm.domain.entity.Settings
import com.y9vad9.bcm.domain.entity.brawlstars.value.ClubTag
import com.y9vad9.bcm.domain.entity.brawlstars.value.Trophies
import com.y9vad9.bcm.domain.entity.telegram.value.TelegramGroupId
import com.y9vad9.bcm.domain.entity.telegram.value.TelegramUserId
import com.y9vad9.bcm.domain.entity.value.CustomMessage
import com.y9vad9.bcm.domain.repository.BrawlStarsRepository
import com.y9vad9.bcm.domain.repository.SettingsRepository
import com.y9vad9.bcm.foundation.validation.annotations.ValidationDelicateApi
import com.y9vad9.bcm.foundation.validation.createUnsafe
import io.github.reactivecircus.cache4k.Cache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.Path
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.time.Duration.Companion.minutes

class SettingsRepositoryImpl(
    private val configFile: Path,
    private val json: Json = Json,
    private val bsRepository: BrawlStarsRepository,
) : SettingsRepository {
    private val cache = Cache.Builder<Unit, Settings>()
        .maximumCacheSize(1)
        .expireAfterWrite(5.minutes)
        .build()

    override suspend fun getSettings(): Settings = withContext(Dispatchers.IO) {
        cache.get(Unit) {
            json.decodeFromString<SettingsSerializable>(configFile.readText())
                .toSettings()
        }
    }

    override suspend fun setSettings(settings: Settings): Unit = withContext(Dispatchers.IO) {
        configFile.writeText(json.encodeToString(settings.toSerializable()))
        cache.put(Unit, settings)
    }

    override suspend fun allowClub(
        tag: ClubTag,
        settings: ClubSettings,
    ): Boolean {
        return try {
            val globalSettings = getSettings()

            if (globalSettings.allowedClubs.containsKey(tag))
                return false

            setSettings(
                globalSettings.copy(
                    allowedClubs = globalSettings.allowedClubs.toMutableMap()
                        .apply {
                            put(tag, settings)
                        }
                        .toMap()
                )
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

@Serializable
private data class SettingsSerializable(
    val admins: List<Long>,
    val allowedClubs: Map<String, ClubSettingsSerializable>,
)

@Serializable
private data class ClubSettingsSerializable(
    val admins: List<Long>,
    val joinViaBotRequest: Boolean,
    val multipleAccountsEnabled: Boolean,
    val minWeeklyTrophies: Int,
    val minMonthlyTrophies: Int,
    val adminCanIgnoreSettings: Boolean,
    val linkedTelegramChat: Long,
    val clubRules: String,
    val chatRules: String,
)

private fun SettingsSerializable.toSettings(): Settings {
    return Settings(
        admins = admins.map { TelegramUserId.createUnsafe(it) },
        allowedClubs = allowedClubs.map { (key, value) ->
            ClubTag.createUnsafe(key) to value.toClubSettings(admins)
        }.toMap(),
    )
}

private fun ClubSettingsSerializable.toClubSettings(
    globalAdmins: List<Long>,
): ClubSettings {
    return ClubSettings(
        admins = admins.map { TelegramUserId.createUnsafe(it) } + globalAdmins.map { TelegramUserId.createUnsafe(it) },
        joinViaBotRequest = joinViaBotRequest,
        multipleAccountsEnabled = multipleAccountsEnabled,
        minWeeklyTrophies = Trophies.createUnsafe(minWeeklyTrophies),
        minMonthlyTrophies = Trophies.createUnsafe(minMonthlyTrophies),
        adminCanIgnoreSettings = adminCanIgnoreSettings,
        linkedTelegramChat = TelegramGroupId.createUnsafe(linkedTelegramChat),
        clubRules = CustomMessage.createUnsafe(clubRules),
        chatRules = CustomMessage.createUnsafe(chatRules),
    )
}

private fun Settings.toSerializable(): SettingsSerializable {
    return SettingsSerializable(
        admins = admins.map { it.value },
        allowedClubs = allowedClubs.map { (tag, settings) ->
            tag.toString() to ClubSettingsSerializable(
                admins = settings.admins.filterNot { id -> id in admins }.map { it.value },
                joinViaBotRequest = settings.joinViaBotRequest,
                multipleAccountsEnabled = settings.multipleAccountsEnabled,
                minWeeklyTrophies = settings.minWeeklyTrophies.value,
                minMonthlyTrophies = settings.minMonthlyTrophies.value,
                adminCanIgnoreSettings = settings.adminCanIgnoreSettings,
                linkedTelegramChat = settings.linkedTelegramChat.value,
                clubRules = settings.clubRules.value,
                chatRules = settings.chatRules.value,
            )
        }.toMap()
    )
}