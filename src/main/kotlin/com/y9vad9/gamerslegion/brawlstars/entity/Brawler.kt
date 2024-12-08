package com.y9vad9.gamerslegion.brawlstars.entity

import kotlinx.serialization.Serializable

@Serializable
data class Brawler(
    val id: Int,
    val name: String,
    val power: Int,
    val rank: Int,
    val trophies: Int,
    val highestTrophies: Int,
    val gears: List<Gear>,
    val starPowers: List<StarPower>,
    val gadgets: List<Gadget>
)