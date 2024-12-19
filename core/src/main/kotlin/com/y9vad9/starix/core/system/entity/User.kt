@file:OptIn(ExperimentalUuidApi::class)

package com.y9vad9.starix.core.system.entity

import com.y9vad9.starix.core.brawlstars.entity.player.Player
import com.y9vad9.starix.core.brawlstars.entity.player.value.PlayerTag
import com.y9vad9.starix.core.system.entity.value.MemberDisplayName
import com.y9vad9.starix.core.telegram.entity.LinkedTelegramAccount
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class User(
    val uuid: Uuid,
    val displayName: MemberDisplayName?,
    val telegramAccount: LinkedTelegramAccount?,
    val bsPlayers: List<com.y9vad9.starix.core.brawlstars.entity.player.Player>?,
) {
    val isBrawlStarsLinked: Boolean
        get() = bsPlayers != null

    val hasMultipleBsLinked: Boolean
        get() = (bsPlayers?.size ?: 0) > 1
}

fun User.getPlayerOrNull(tag: PlayerTag): com.y9vad9.starix.core.brawlstars.entity.player.Player? =
    bsPlayers?.firstOrNull { player -> player.tag.toString() == tag.toString() }