package com.y9vad9.bcm.data.brawlstars.entity

import kotlinx.serialization.Serializable

@Serializable
data class BsBrawler(
    val id: Int,
    val name: String,
    val power: Int,
    val rank: Int,
    val trophies: Int,
    val highestTrophies: Int,
    val gears: List<BsGear>,
    val starPowers: List<BsStarPower>,
    val gadgets: List<BsGadget>
)