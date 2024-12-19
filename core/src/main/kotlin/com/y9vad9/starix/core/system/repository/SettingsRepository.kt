package com.y9vad9.starix.core.system.repository

import com.y9vad9.bcm.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.bcm.core.system.entity.ClubSettings
import com.y9vad9.bcm.core.system.entity.Settings

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