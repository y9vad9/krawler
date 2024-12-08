package com.y9vad9.gamerslegion.brawlstars.entity

import kotlinx.serialization.Serializable

@Serializable
data class Gear(
    val id: Int,
    val name: String,
    val level: Int? = null
)