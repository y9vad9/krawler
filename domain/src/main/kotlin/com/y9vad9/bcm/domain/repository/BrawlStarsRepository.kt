package com.y9vad9.bcm.domain.repository

import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsClub
import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsPlayer
import com.y9vad9.bcm.domain.entity.brawlstars.value.ClubTag
import com.y9vad9.bcm.domain.entity.brawlstars.value.PlayerTag

interface BrawlStarsRepository {
    suspend fun getPlayer(tag: PlayerTag, withInvalidate: Boolean = false): Result<BrawlStarsPlayer?>
    suspend fun getClub(tag: ClubTag, withInvalidate: Boolean = false): Result<BrawlStarsClub?>
}