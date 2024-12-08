package com.y9vad9.bcm.data.brawlstars.entity

import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsClub
import com.y9vad9.bcm.domain.entity.brawlstars.value.ClubName
import com.y9vad9.bcm.domain.entity.brawlstars.value.ClubTag
import com.y9vad9.bcm.domain.entity.brawlstars.value.ClubType
import com.y9vad9.bcm.domain.entity.brawlstars.value.Trophies
import com.y9vad9.bcm.domain.entity.value.CustomMessage
import com.y9vad9.bcm.foundation.validation.annotations.ValidationDelicateApi
import com.y9vad9.bcm.foundation.validation.createUnsafe
import kotlinx.serialization.Serializable

@Serializable
data class BsClub(
    val tag: String,
    val name: String,
    val description: String,
    val requiredTrophies: Int,
    val trophies: Int,
    val type: String,
    val members: List<BsMember>
)

@OptIn(ValidationDelicateApi::class)
internal fun BsClub.toBrawlStarsClub(): BrawlStarsClub {
    return BrawlStarsClub(
        tag = ClubTag.createUnsafe(tag),
        name = ClubName.createUnsafe(name),
        description = CustomMessage.createUnsafe(description),
        members = members.map { it.toBrawlStarsClubMember() },
        requiredTrophies = Trophies.createUnsafe(requiredTrophies),
        trophies = Trophies.createUnsafe(trophies),
        type = ClubType.valueOf(type.uppercase()),
    )
}

internal fun BrawlStarsClub.serializable(): BsClub {
    return BsClub(
        tag = tag.toString(),
        name = name.value,
        description = description.value,
        requiredTrophies = requiredTrophies.value,
        trophies = trophies.value,
        type = type.name.lowercase(),
        members = members.map { it.serializable() },
    )
}