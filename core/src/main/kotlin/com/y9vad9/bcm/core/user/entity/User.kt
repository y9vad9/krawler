@file:OptIn(ExperimentalUuidApi::class)

package com.y9vad9.bcm.core.user.entity

import com.y9vad9.bcm.core.brawlstars.entity.player.Player
import com.y9vad9.bcm.core.brawlstars.entity.player.value.PlayerTag
import com.y9vad9.bcm.core.telegram.entity.LinkedTelegramAccount
import com.y9vad9.bcm.core.user.entity.value.MemberDisplayName
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class User(
    val uuid: Uuid,
    val displayName: MemberDisplayName?,
    val telegramAccount: LinkedTelegramAccount?,
    val bsPlayers: List<Player>?,
) {
    val isBrawlStarsLinked: Boolean
        get() = bsPlayers != null

    val hasMultipleBsLinked: Boolean
        get() = (bsPlayers?.size ?: 0) > 1
}

fun User.getPlayerOrNull(tag: PlayerTag): Player? =
    bsPlayers?.firstOrNull { player -> player.tag == tag }