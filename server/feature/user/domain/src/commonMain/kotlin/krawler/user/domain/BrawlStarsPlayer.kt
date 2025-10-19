package krawler.user.domain

import krawler.core.domain.Entity
import krawler.core.domain.EntityId

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
@Entity
public class BrawlStarsPlayer(
    @EntityId
    public val tag: BrawlStarsPlayerTag,
    public val name: BrawlStarsPlayerName,
) {
    /**
     * Returns copy of this entity with new specified name.
     */
    public fun rename(newName: BrawlStarsPlayerName): BrawlStarsPlayer = BrawlStarsPlayer(tag, newName)

    override fun equals(other: Any?): Boolean =
        this === other || (other is BrawlStarsPlayer && tag == other.tag && name == other.name)
    override fun hashCode(): Int = 31 * tag.hashCode() + name.hashCode()
    override fun toString(): String = "BrawlStarsPlayer(tag=$tag, name=$name)"
}
