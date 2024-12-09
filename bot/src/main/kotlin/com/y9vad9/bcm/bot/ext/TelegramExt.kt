package com.y9vad9.bcm.bot.ext

import com.y9vad9.bcm.domain.entity.telegram.value.TelegramUserId
import com.y9vad9.bcm.foundation.validation.annotations.ValidationDelicateApi
import com.y9vad9.bcm.foundation.validation.createUnsafe
import dev.inmo.tgbotapi.types.IdChatIdentifier

@OptIn(ValidationDelicateApi::class)
internal fun IdChatIdentifier.asTelegramUserId(): TelegramUserId {
    return TelegramUserId.createUnsafe(chatId.long)
}