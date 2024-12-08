package com.y9vad9.bcm.domain.entity

import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsClub
import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsClubMember
import com.y9vad9.bcm.domain.entity.brawlstars.value.PlayerTag

data class Club(
    val bs: BrawlStarsClub,
    val settings: ClubSettings,
)

fun Club.getBsMemberOrNull(tag: PlayerTag): BrawlStarsClubMember? {
    return bs.members.firstOrNull { member -> member.tag == tag }
}

fun Club.getBsMember(tag: PlayerTag): BrawlStarsClubMember {
    return getBsMemberOrNull(tag) ?: error("Player with tag $tag should be present.")
}