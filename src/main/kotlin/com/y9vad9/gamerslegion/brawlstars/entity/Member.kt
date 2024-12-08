package com.y9vad9.gamerslegion.brawlstars.entity

import kotlinx.serialization.Serializable

@Serializable
data class Member(
    val tag: String,
    val name: String,
    val nameColor: String,
    val role: String,
    val trophies: Int,
    val icon: Icon
)