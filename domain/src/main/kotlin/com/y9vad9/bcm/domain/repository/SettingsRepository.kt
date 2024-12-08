package com.y9vad9.bcm.domain.repository

import com.y9vad9.bcm.domain.entity.ClubSettings
import com.y9vad9.bcm.domain.entity.Settings
import com.y9vad9.bcm.domain.entity.brawlstars.value.ClubTag

interface SettingsRepository {
    suspend fun getSettings(): Settings
    suspend fun setSettings(settings: Settings)

    suspend fun allowClub(
        tag: ClubTag,
        settings: ClubSettings,
    ): Boolean
}

suspend fun SettingsRepository.getClubSettings(tag: ClubTag): ClubSettings =
    getSettings().allowedClubs[tag] ?: error("Invalid tag, no settings provided.")