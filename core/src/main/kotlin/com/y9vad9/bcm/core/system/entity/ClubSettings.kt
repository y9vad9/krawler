package com.y9vad9.bcm.core.system.entity

import com.y9vad9.bcm.core.brawlstars.entity.event.value.Trophies
import com.y9vad9.bcm.core.common.entity.value.CustomMessage
import com.y9vad9.bcm.core.telegram.entity.value.TelegramGroupId
import com.y9vad9.bcm.core.telegram.entity.value.TelegramUserId
import kotlinx.serialization.Serializable

@Serializable
data class ClubSettings(
    val admins: List<TelegramUserId>,
    val joinViaBotRequest: Boolean,
    val joinWithoutRequirementsCheck: Boolean,
    val multipleAccountsEnabled: Boolean,
    val minWeeklyTrophies: Trophies,
    val minMonthlyTrophies: Trophies,
    val adminCanIgnoreSettings: Boolean,
    val linkedTelegramChat: TelegramGroupId,
    val clubRules: CustomMessage,
    val chatRules: CustomMessage,
)