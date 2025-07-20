package com.y9vad9.brawlex.user.domain

import com.y9vad9.brawlex.user.domain.value.LinkedPlayerName
import com.y9vad9.brawlex.user.domain.value.LinkedPlayerTag
import com.y9vad9.valdi.domain.DomainEntity

/**
 * Represents a player linked to a user within the system.
 *
 * This domain entity encapsulates the unique identifier of the player ([tag]) and the player's display name ([name]).
 * The [tag] uniquely identifies the player in the external system (e.g., Brawl Stars),
 * while [name] provides a human-readable label for that player.
 *
 * Being a domain entity, instances are primarily identified by their [tag], which acts as the identity property.
 *
 * Use this class to manage, link, and reference players associated with users in the domain.
 */
@DomainEntity
public data class LinkedPlayer(
    @DomainEntity.Id
    val tag: LinkedPlayerTag,
    val name: LinkedPlayerName,
)
