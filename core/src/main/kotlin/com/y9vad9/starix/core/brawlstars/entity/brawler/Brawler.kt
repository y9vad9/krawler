package com.y9vad9.starix.core.brawlstars.entity.brawler

import com.y9vad9.starix.core.brawlstars.entity.brawler.value.BrawlerId
import com.y9vad9.starix.core.brawlstars.entity.brawler.value.BrawlerName
import com.y9vad9.starix.core.brawlstars.entity.brawler.value.BrawlerRank
import com.y9vad9.starix.core.brawlstars.entity.brawler.value.PowerLevel
import com.y9vad9.starix.core.brawlstars.entity.club.Club
import com.y9vad9.starix.core.brawlstars.entity.event.value.Trophies
import com.y9vad9.starix.core.brawlstars.entity.player.PlayerIcon
import kotlinx.serialization.Serializable


@Serializable
data class Brawler(
    val id: BrawlerId,
    val name: BrawlerName,
    val power: PowerLevel,
    val rank: BrawlerRank,
    val trophies: Trophies,
    val highestTrophies: Trophies,
    val gears: List<Gear>,
    val starPowers: List<StarPower>,
    val gadgets: List<Gadget>,
) {
    @Serializable
    data class View(
        val id: BrawlerId,
        val name: BrawlerName,
        val starPowers: List<StarPower>,
        val gadgets: List<Gadget>,
    )

    @Serializable
    data class Ranking(
        val id: BrawlerId,
        val name: BrawlerName,
        val icon: PlayerIcon,
        val trophies: Trophies,
        val club: com.y9vad9.starix.core.brawlstars.entity.club.Club.View,
    )
}

val Brawler.isMaxRanked: Boolean get() = rank == BrawlerRank.MAX

