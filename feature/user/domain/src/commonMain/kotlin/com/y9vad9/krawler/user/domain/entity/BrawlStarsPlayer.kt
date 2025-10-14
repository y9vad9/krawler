package com.y9vad9.krawler.user.domain.entity

import com.y9vad9.krawler.user.domain.value.BrawlStarsPlayerName
import com.y9vad9.krawler.user.domain.value.BrawlStarsPlayerTag
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
public class BrawlStarsPlayer(
    @DomainEntity.Id
    public val tag: BrawlStarsPlayerTag,
    public val name: BrawlStarsPlayerName,
) {
    /**
     * Returns copy of this entity with new specified name.
     */
    public fun withNewName(newName: BrawlStarsPlayerName): BrawlStarsPlayer = BrawlStarsPlayer(tag, newName)

    override fun equals(other: Any?): Boolean =
        this === other || (other is BrawlStarsPlayer && tag == other.tag && name == other.name)
    override fun hashCode(): Int = 31 * tag.hashCode() + name.hashCode()
    override fun toString(): String = "BrawlStarsPlayer(tag=$tag, name=$name)"
}
