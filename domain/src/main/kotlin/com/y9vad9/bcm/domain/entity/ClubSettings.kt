package com.y9vad9.bcm.domain.entity

import com.y9vad9.bcm.domain.entity.brawlstars.value.Trophies
import com.y9vad9.bcm.domain.entity.telegram.value.TelegramGroupId
import com.y9vad9.bcm.domain.entity.telegram.value.TelegramUserId
import com.y9vad9.bcm.domain.entity.value.CustomMessage

data class ClubSettings(
    val admins: List<TelegramUserId>,
    val joinViaBotRequest: Boolean,
    val multipleAccountsEnabled: Boolean,
    val minWeeklyTrophies: Trophies,
    val minMonthlyTrophies: Trophies,
    val adminCanIgnoreSettings: Boolean,
    val linkedTelegramChat: TelegramGroupId,
    val clubRules: CustomMessage,
    val chatRules: CustomMessage,
)