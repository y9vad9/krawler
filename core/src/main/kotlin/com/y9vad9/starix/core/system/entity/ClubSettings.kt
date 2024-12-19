package com.y9vad9.starix.core.system.entity

import com.y9vad9.bcm.core.brawlstars.entity.event.value.Trophies
import com.y9vad9.bcm.core.common.entity.value.CustomMessage
import com.y9vad9.bcm.core.system.entity.value.LanguageCode
import com.y9vad9.bcm.core.telegram.entity.value.TelegramGroupId
import com.y9vad9.bcm.core.telegram.entity.value.TelegramUserId
import com.y9vad9.starix.foundation.validation.annotations.ValidationDelicateApi
import com.y9vad9.starix.foundation.validation.createUnsafe
import kotlinx.serialization.Serializable

@Serializable
data class ClubSettings @OptIn(ValidationDelicateApi::class) constructor(
    val admins: List<TelegramUserId> = emptyList(),
    val joinViaBotRequest: Boolean = false,
    val joinWithoutRequirementsCheck: Boolean = false,
    val multipleAccountsEnabled: Boolean = false,
    val minMonthlyTrophies: Trophies = Trophies.ZERO,
    val adminCanIgnoreSettings: Boolean = false,
    val linkedTelegramChat: TelegramGroupId? = null,
    val clubRules: CustomMessage = CustomMessage.createUnsafe(NOT_SPECIFIED_MESSAGE),
    val chatRules: CustomMessage = CustomMessage.createUnsafe(NOT_SPECIFIED_MESSAGE),
    val defaultLanguage: LanguageCode = LanguageCode.ENGLISH,
)

private const val NOT_SPECIFIED_MESSAGE = "Not Specified."