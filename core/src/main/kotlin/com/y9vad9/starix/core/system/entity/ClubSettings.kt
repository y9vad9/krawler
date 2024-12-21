package com.y9vad9.starix.core.system.entity

import com.y9vad9.starix.core.brawlstars.entity.event.value.Trophies
import com.y9vad9.starix.core.common.entity.value.CustomMessage
import com.y9vad9.starix.core.system.entity.value.LanguageCode
import com.y9vad9.starix.core.telegram.entity.value.TelegramGroupId
import com.y9vad9.starix.core.telegram.entity.value.TelegramUserId
import com.y9vad9.starix.foundation.validation.annotations.ValidationDelicateApi
import com.y9vad9.starix.foundation.validation.createUnsafe
import kotlinx.serialization.Serializable

@Serializable
data class ClubSettings(
    val admins: List<TelegramUserId> = emptyList(),
    val joinViaBotRequest: Boolean = false,
    val joinWithoutRequirementsCheck: Boolean = false,
    val multipleAccountsEnabled: Boolean = false,
    val minMonthlyTrophies: Trophies = Trophies.ZERO,
    val adminCanIgnoreSettings: Boolean = false,
    val linkedTelegramChat: TelegramGroupId? = null,
    val clubRules: LocalizableEntity<CustomMessage> = NOT_SPECIFIED_LOCALIZABLE,
    val chatRules: LocalizableEntity<CustomMessage> = NOT_SPECIFIED_LOCALIZABLE,
    val contactsInfo: LocalizableEntity<CustomMessage> = NOT_SPECIFIED_LOCALIZABLE,
    /**
     * Aside using language as a default fallback, it is also used for sending any
     * bot's specific messages, for example, monthly feedback.
     */
    val defaultLanguage: LanguageCode = LanguageCode.ENGLISH,
)

@OptIn(ValidationDelicateApi::class)
private val NOT_SPECIFIED_LOCALIZABLE = LocalizableEntity(LanguageCode.ENGLISH to CustomMessage.createUnsafe("Not specified."))