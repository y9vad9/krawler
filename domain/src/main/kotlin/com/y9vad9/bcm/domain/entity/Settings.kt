package com.y9vad9.bcm.domain.entity

import com.y9vad9.bcm.domain.entity.brawlstars.value.ClubTag
import com.y9vad9.bcm.domain.entity.telegram.value.TelegramUserId

data class Settings(
    val admins: List<TelegramUserId>,
    val allowedClubs: Map<ClubTag, ClubSettings>,
)