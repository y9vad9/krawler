package com.y9vad9.starix.core.brawlstars.entity.player

import com.y9vad9.starix.core.brawlstars.entity.brawler.Brawler
import com.y9vad9.starix.core.brawlstars.entity.club.Club
import com.y9vad9.starix.core.brawlstars.entity.club.ClubMember
import com.y9vad9.starix.core.brawlstars.entity.club.value.PlayerRole
import com.y9vad9.starix.core.brawlstars.entity.event.value.RankingPosition
import com.y9vad9.starix.core.brawlstars.entity.event.value.Trophies
import com.y9vad9.starix.core.brawlstars.entity.player.value.PlayerName
import com.y9vad9.starix.core.brawlstars.entity.player.value.PlayerTag
import com.y9vad9.starix.core.common.entity.value.Count
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val tag: PlayerTag,
    val name: PlayerName,
    val icon: com.y9vad9.starix.core.brawlstars.entity.player.PlayerIcon,
    val trophies: Trophies,
    val highestTrophies: Trophies,
    val expLevel: Count,
    val expPoints: Count,
    val isQualifiedFromChampionshipChallenge: Boolean,
    @SerialName("3vs3Victories") val threeVsThreeVictories: Count,
    val soloVictories: Count,
    val duoVictories: Count,
    val bestRoboRumbleTime: Count,
    val bestTimeAsBigBrawler: Count,
    val club: _root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club.View?,
    val brawlers: List<Brawler>,
) {
    @Serializable
    data class Ranking(
        val tag: PlayerTag,
        val name: PlayerName,
        val icon: com.y9vad9.starix.core.brawlstars.entity.player.PlayerIcon,
        val trophies: Trophies,
        val rank: RankingPosition,
        val club: _root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club.View,
    )
}

fun com.y9vad9.starix.core.brawlstars.entity.player.Player.toClubMember(): ClubMember {
    return ClubMember(
        tag = tag,
        name = name,
        role = PlayerRole.UNKNOWN,
        trophies = trophies,
        icon = icon,
    )
}