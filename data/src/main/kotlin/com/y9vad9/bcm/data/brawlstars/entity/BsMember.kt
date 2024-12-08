package com.y9vad9.bcm.data.brawlstars.entity

import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsClubMember
import com.y9vad9.bcm.domain.entity.brawlstars.value.ClubTag
import com.y9vad9.bcm.domain.entity.brawlstars.value.PlayerName
import com.y9vad9.bcm.domain.entity.brawlstars.value.PlayerRole
import com.y9vad9.bcm.domain.entity.brawlstars.value.PlayerTag
import com.y9vad9.bcm.domain.entity.brawlstars.value.Trophies
import com.y9vad9.bcm.foundation.validation.annotations.ValidationDelicateApi
import com.y9vad9.bcm.foundation.validation.createUnsafe
import kotlinx.serialization.Serializable

@Serializable
data class BsMember(
    val tag: String,
    val name: String,
    val nameColor: String,
    val role: String,
    val trophies: Int,
    val icon: BsIcon
)

@OptIn(ValidationDelicateApi::class)
internal fun BsMember.toBrawlStarsClubMember(): BrawlStarsClubMember {
    return BrawlStarsClubMember(
        tag = PlayerTag.createUnsafe(tag),
        name = PlayerName.createUnsafe(name),
        trophies = Trophies.createUnsafe(trophies),
        role = PlayerRole.valueOf(role.uppercase()),
    )
}

internal fun BrawlStarsClubMember.serializable(): BsMember {
    return BsMember(
        tag = tag.toString(),
        name = name.value,
        nameColor = "",
        role = role.name.lowercase(),
        trophies = trophies.value,
        icon = BsIcon(-1)
    )
}