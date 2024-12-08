package com.y9vad9.gamerslegion.brawlstars.entity

import kotlinx.serialization.Serializable

@Serializable
data class Battle(
    val battleTime: String,
    val event: Event
)