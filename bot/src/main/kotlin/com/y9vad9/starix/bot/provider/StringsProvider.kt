package com.y9vad9.starix.bot.provider

import com.y9vad9.starix.core.system.entity.value.LanguageCode
import com.y9vad9.starix.localization.Strings
import dev.inmo.tgbotapi.types.IdChatIdentifier
import java.util.*

interface StringsProvider {
    suspend fun getStrings(chatId: IdChatIdentifier): Strings
    suspend fun setStrings(chatId: IdChatIdentifier, code: LanguageCode): Strings

    suspend fun knowingDefinetely(chatId: IdChatIdentifier): Boolean

    fun getAvailableStrings(): List<Strings>
}