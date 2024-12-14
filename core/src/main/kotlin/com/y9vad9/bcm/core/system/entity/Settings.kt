package com.y9vad9.bcm.core.system.entity

import com.y9vad9.bcm.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.bcm.core.telegram.entity.value.TelegramUserId
import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    val admins: List<TelegramUserId> = emptyList(),
    val allowedClubs: Map<ClubTag, ClubSettings> = emptyMap(),
)