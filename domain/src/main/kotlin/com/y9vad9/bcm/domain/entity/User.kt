@file:OptIn(ExperimentalUuidApi::class)

package com.y9vad9.bcm.domain.entity

import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsPlayer
import com.y9vad9.bcm.domain.entity.brawlstars.value.PlayerTag
import com.y9vad9.bcm.domain.entity.telegram.LinkedTelegramAccount
import com.y9vad9.bcm.domain.entity.value.MemberDisplayName
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class User(
    val uuid: Uuid,
    val displayName: MemberDisplayName?,
    val telegramAccount: LinkedTelegramAccount?,
    val bsPlayers: List<BrawlStarsPlayer>?,
) {
    val isBrawlStarsLinked: Boolean
        get() = bsPlayers != null

    val hasMultipleBsLinked: Boolean
        get() = (bsPlayers?.size ?: 0) > 1
}

fun User.getPlayerOrNull(tag: PlayerTag): BrawlStarsPlayer? =
    bsPlayers?.firstOrNull { player -> player.tag == tag }