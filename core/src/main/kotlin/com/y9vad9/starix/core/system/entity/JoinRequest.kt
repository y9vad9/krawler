package com.y9vad9.starix.core.system.entity

import com.y9vad9.bcm.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.bcm.core.brawlstars.entity.player.value.PlayerTag
import com.y9vad9.bcm.core.common.entity.value.CustomMessage
import com.y9vad9.bcm.core.telegram.entity.value.TelegramUserId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class JoinRequest(
    val id: Uuid,
    val playerTag: PlayerTag,
    val clubTag: ClubTag,
    val message: CustomMessage,
    val tgId: TelegramUserId,
)