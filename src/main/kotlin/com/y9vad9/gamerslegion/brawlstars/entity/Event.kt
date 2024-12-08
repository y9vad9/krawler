package com.y9vad9.gamerslegion.brawlstars.entity

import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val mode: String,
    val id: Int,
    val map: Map<String, String>
)