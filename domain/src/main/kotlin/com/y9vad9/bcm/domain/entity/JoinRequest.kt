package com.y9vad9.bcm.domain.entity

import com.y9vad9.bcm.domain.entity.brawlstars.value.ClubTag
import com.y9vad9.bcm.domain.entity.brawlstars.value.PlayerTag
import com.y9vad9.bcm.domain.entity.telegram.value.TelegramUserId
import com.y9vad9.bcm.domain.entity.value.CustomMessage
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