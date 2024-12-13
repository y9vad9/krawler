package com.y9vad9.bcm.core.brawlstars.entity.player

import com.y9vad9.bcm.core.brawlstars.entity.brawler.Brawler
import com.y9vad9.bcm.core.brawlstars.entity.club.Club
import com.y9vad9.bcm.core.brawlstars.entity.event.value.RankingPosition
import com.y9vad9.bcm.core.brawlstars.entity.event.value.Trophies
import com.y9vad9.bcm.core.brawlstars.entity.player.value.PlayerName
import com.y9vad9.bcm.core.brawlstars.entity.player.value.PlayerTag
import com.y9vad9.bcm.core.common.entity.value.Count
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val tag: PlayerTag,
    val name: PlayerName,
    val icon: PlayerIcon,
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
    val club: Club.View?,
    val brawlers: List<Brawler>,
) {
    @Serializable
    data class Ranking(
        val tag: PlayerTag,
        val name: PlayerName,
        val icon: PlayerIcon,
        val trophies: Trophies,
        val rank: RankingPosition,
        val club: Club.View,
    )
}