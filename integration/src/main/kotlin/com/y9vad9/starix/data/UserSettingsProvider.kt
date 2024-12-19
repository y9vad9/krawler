package com.y9vad9.starix.data

import com.y9vad9.starix.bot.provider.StringsProvider
import com.y9vad9.starix.bot.provider.TimeZoneProvider
import com.y9vad9.starix.data.database.UsersTable
import com.y9vad9.starix.localization.Strings
import dev.inmo.tgbotapi.types.ChatIdentifier
import dev.inmo.tgbotapi.types.IdChatIdentifier
import io.github.reactivecircus.cache4k.Cache
import java.time.ZoneId
import kotlin.time.Duration.Companion.minutes

class UserSettingsProvider(
    private val usersTable: UsersTable,
    private val availableStrings: Map<String, Strings>,
    private val defaultStrings: Strings,
    private val defaultTimeZone: ZoneId,
) : StringsProvider, TimeZoneProvider {
    private val entitiesCache = Cache.Builder<IdChatIdentifier, UsersTable.Entity>()
        .maximumCacheSize(200)
        .expireAfterAccess(10.minutes)
        .build()


    override suspend fun getStrings(chatId: IdChatIdentifier): Strings {
        return try {
            entitiesCache.get(chatId) {
                usersTable.get(chatId.chatId.long)!!
            }.languageCode?.let { availableStrings[it] } ?: defaultStrings
        } catch (e: Exception) {
            defaultStrings
        }
    }

    override suspend fun getTimeZone(id: IdChatIdentifier): ZoneId {
        return try {
            entitiesCache.get(id) {
                usersTable.get(id.chatId.long)!!
            }.languageCode?.let { ZoneId.of(it) } ?: defaultTimeZone
        } catch (e: Exception) {
            defaultTimeZone
        }
    }

}