package com.y9vad9.brawlex.user.domain

import com.y9vad9.brawlex.user.domain.value.LinkedPlayerTag
import com.y9vad9.brawlex.user.domain.value.LinkedPlayers
import com.y9vad9.brawlex.user.domain.value.UserId
import com.y9vad9.brawlex.user.domain.value.UserName
import com.y9vad9.valdi.domain.AggregateRoot

/**
 * Aggregate root representing a user in the system.
 *
 * Encapsulates the user's identity, display name, and the collection of linked Brawl Stars players.
 * This aggregate enforces rules around managing linked players, ensuring consistency such as:
 * - Preventing duplicate player links in addition.
 * - Allowing explicit refresh of player data only if already linked.
 * - Safe removal of linked players by tag.
 *
 * All mutation-like operations return a new [User] instance reflecting the change,
 * preserving immutability and supporting functional style updates.
 *
 * Use the provided methods to update user information and linked players explicitly,
 * favoring clarity and explicit intent over implicit or "magical" state changes.
 */
@AggregateRoot
public class User(
    public val id: UserId,
    public val name: UserName,
    public val linkedPlayers: LinkedPlayers,
) {

    /** Returns a copy with updated username */
    public fun withNewName(newName: UserName): User =
        User(id, newName, linkedPlayers)

    /**
     * Adds a new linked player.
     * Throws if player is already linked.
     */
    public fun withAddedPlayer(player: LinkedPlayer): User =
        User(id, name, linkedPlayers.with(player))

    /**
     * Refreshes an existing linked player.
     * Throws if player is not linked.
     *
     * @throws IllegalArgumentException if player is not linked.
     * @see LinkedPlayers.withRefreshed
     */
    @Throws(IllegalArgumentException::class)
    public fun withRefreshedPlayer(player: LinkedPlayer): User =
        User(id, name, linkedPlayers.withRefreshed(player))

    /**
     * Returns a copy with the player unlinked by [tag].
     * Returns the same user if the player is not found.
     */
    public fun withoutLinkedPlayer(tag: LinkedPlayerTag): User =
        if (!linkedPlayers.has(tag)) this
        else User(id, name, linkedPlayers.without(tag))

    /** Returns a copy with all linked players removed. */
    public fun withoutAllLinkedPlayers(): User =
        User(id, name, linkedPlayers.withoutAll())
}
