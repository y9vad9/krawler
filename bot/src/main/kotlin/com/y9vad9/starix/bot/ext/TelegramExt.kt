package com.y9vad9.starix.bot.ext

import com.y9vad9.starix.core.telegram.entity.value.TelegramUserId
import com.y9vad9.starix.foundation.validation.annotations.ValidationDelicateApi
import com.y9vad9.starix.foundation.validation.createUnsafe
import dev.inmo.tgbotapi.types.IdChatIdentifier

@OptIn(ValidationDelicateApi::class)
internal fun IdChatIdentifier.asTelegramUserId(): TelegramUserId {
    return TelegramUserId.createUnsafe(chatId.long)
}