package com.y9vad9.bcm.domain.entity.brawlstars

import com.y9vad9.bcm.domain.entity.brawlstars.value.ClubType
import com.y9vad9.bcm.domain.entity.brawlstars.value.ClubName
import com.y9vad9.bcm.domain.entity.brawlstars.value.ClubTag
import com.y9vad9.bcm.domain.entity.brawlstars.value.Trophies
import com.y9vad9.bcm.domain.entity.value.CustomMessage

data class BrawlStarsClub(
    val tag: ClubTag,
    val name: ClubName,
    val description: CustomMessage,
    val members: List<BrawlStarsClubMember>,
    val requiredTrophies: Trophies,
    val trophies: Trophies,
    val type: ClubType,
) {
    val canJoin: Boolean get() = members.size < 30 && type == ClubType.OPEN
}