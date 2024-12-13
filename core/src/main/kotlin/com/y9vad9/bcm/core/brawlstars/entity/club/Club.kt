package com.y9vad9.bcm.core.brawlstars.entity.club

import com.y9vad9.bcm.core.brawlstars.entity.club.value.ClubName
import com.y9vad9.bcm.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.bcm.core.brawlstars.entity.club.value.ClubType
import com.y9vad9.bcm.core.brawlstars.entity.event.value.RankingPosition
import com.y9vad9.bcm.core.brawlstars.entity.event.value.Trophies
import kotlinx.serialization.Serializable

@Serializable
data class Club(
    val tag: ClubTag,
    val name: ClubName,
    val description: String,
    val requiredTrophies: Trophies,
    val trophies: Trophies,
    val type: ClubType,
    val members: List<ClubMember>,
) {
    @Serializable
    data class View(
        // not exist only in the ranking top
        val tag: ClubTag? = null,
        val name: ClubName,
    )

    @Serializable
    data class Ranking(
        val clubTag: ClubTag,
        val name: ClubName,
        val trophies: Trophies,
        val rank: RankingPosition,

        )
}

