package com.y9vad9.bcm.domain.entity.brawlstars

import com.y9vad9.bcm.domain.entity.brawlstars.value.PlayerName
import com.y9vad9.bcm.domain.entity.brawlstars.value.PlayerRole
import com.y9vad9.bcm.domain.entity.brawlstars.value.PlayerTag
import com.y9vad9.bcm.domain.entity.brawlstars.value.Trophies

data class BrawlStarsClubMember(
    val tag: PlayerTag,
    val name: PlayerName,
    val trophies: Trophies,
    val role: PlayerRole,
)