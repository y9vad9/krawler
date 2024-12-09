package com.y9vad9.bcm.domain.entity.brawlstars

import com.y9vad9.bcm.domain.entity.brawlstars.value.PlayerName
import com.y9vad9.bcm.domain.entity.brawlstars.value.PlayerRole
import com.y9vad9.bcm.domain.entity.brawlstars.value.PlayerTag
import com.y9vad9.bcm.domain.entity.brawlstars.value.Trophies
import com.y9vad9.bcm.domain.entity.value.Count

data class BrawlStarsPlayer(
    val tag: PlayerTag,
    val name: PlayerName,
    val club: BrawlStarsClub?,
    val trophies: Trophies,
    val highestTrophies: Trophies,
    val threeVsThreeVictories: Count,
    val soloVictories: Count,
    val duoVictories: Count,
)

val BrawlStarsPlayer.canKickPlayersInClub: Boolean get() {
    val clubMember = asClubMember()
    return when (clubMember.role) {
        PlayerRole.PRESIDENT,
        PlayerRole.VICE_PRESIDENT -> true
        else -> false
    }
}

fun BrawlStarsPlayer.asClubMember(): BrawlStarsClubMember? {
    return club?.members?.first { member -> member.tag.toString() == tag.toString() }
}