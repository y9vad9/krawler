package com.y9vad9.starix.core.brawlstars.entity.club

import com.y9vad9.starix.core.brawlstars.entity.club.value.ClubName
import com.y9vad9.starix.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.starix.core.brawlstars.entity.club.value.ClubType
import com.y9vad9.starix.core.brawlstars.entity.event.value.RankingPosition
import com.y9vad9.starix.core.brawlstars.entity.event.value.Trophies
import kotlinx.serialization.Serializable

@Serializable
data class Club(
    val tag: ClubTag,
    val name: ClubName,
    val description: String,
    val requiredTrophies: Trophies,
    val trophies: Trophies,
    val type: ClubType,
    val members: List<com.y9vad9.starix.core.brawlstars.entity.club.ClubMember>,
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

val com.y9vad9.starix.core.brawlstars.entity.club.Club.hasFreeSeats: Boolean get() = members.size < 30

fun com.y9vad9.starix.core.brawlstars.entity.club.Club.toView(): com.y9vad9.starix.core.brawlstars.entity.club.Club.View {
    return _root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club.View(
        tag = tag,
        name = name,
    )
}
