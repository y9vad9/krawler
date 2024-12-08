package com.y9vad9.bcm.data.brawlstars.entity

import com.y9vad9.bcm.domain.entity.Club
import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsClub
import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsPlayer
import com.y9vad9.bcm.domain.entity.brawlstars.value.PlayerName
import com.y9vad9.bcm.domain.entity.brawlstars.value.PlayerRole
import com.y9vad9.bcm.domain.entity.brawlstars.value.PlayerTag
import com.y9vad9.bcm.domain.entity.brawlstars.value.Trophies
import com.y9vad9.bcm.foundation.validation.annotations.ValidationDelicateApi
import com.y9vad9.bcm.foundation.validation.createUnsafe
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BsPlayer(
    val tag: String,
    val name: String,
    val icon: BsIcon,
    val trophies: Int,
    val highestTrophies: Int,
    val expLevel: Int,
    val expPoints: Int,
    val isQualifiedFromChampionshipChallenge: Boolean,
    @SerialName("3vs3Victories") val threeVsThreeVictories: Int,
    val soloVictories: Int,
    val duoVictories: Int,
    val bestRoboRumbleTime: Int,
    val bestTimeAsBigBrawler: Int,
    val club: BsClub,
    val brawlers: List<BsBrawler>
)

@OptIn(ValidationDelicateApi::class)
internal fun BsPlayer.toBrawlStarsPlayer(club: BrawlStarsClub?): BrawlStarsPlayer {
    return BrawlStarsPlayer(
        tag = PlayerTag.createUnsafe(tag),
        name = PlayerName.createUnsafe(name),
        club = club,
        trophies = Trophies.createUnsafe(trophies),
    )
}