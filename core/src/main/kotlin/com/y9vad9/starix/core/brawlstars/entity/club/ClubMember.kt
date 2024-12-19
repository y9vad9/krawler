package com.y9vad9.starix.core.brawlstars.entity.club

import com.y9vad9.bcm.core.brawlstars.entity.club.value.PlayerRole
import com.y9vad9.bcm.core.brawlstars.entity.event.value.Trophies
import com.y9vad9.bcm.core.brawlstars.entity.player.PlayerIcon
import com.y9vad9.bcm.core.brawlstars.entity.player.value.PlayerName
import com.y9vad9.bcm.core.brawlstars.entity.player.value.PlayerTag
import kotlinx.serialization.Serializable

@Serializable
data class ClubMember(
    val tag: PlayerTag,
    val name: PlayerName,
    val role: PlayerRole,
    val trophies: Trophies,
    val icon: PlayerIcon,
)