package krawler.user.domain

/**
 * Represents a Brawl Stars player in the system.
 *
 * This domain entity encapsulates the unique identifier of the player ([tag]) and the player's display name ([name]).
 * The [tag] uniquely identifies the player in the external system (e.g., Brawl Stars),
 * while [name] provides a human-readable label for that player.
 *
 * Being a domain entity, instances are primarily identified by their [tag], which acts as the identity property.
 *
 * Use this class to manage, link, and reference players associated with users in the domain.
 */
public class Player(
    public val tag: PlayerTag,
    public val name: PlayerName,
) {
    /**
     * Returns copy of this entity with new specified name.
     */
    public fun rename(newName: PlayerName): Player = Player(tag, newName)

    override fun equals(other: Any?): Boolean =
        this === other || (other is Player && tag == other.tag && name == other.name)
    override fun hashCode(): Int = 31 * tag.hashCode() + name.hashCode()
    override fun toString(): String = "Player(tag=$tag, name=$name)"
}
