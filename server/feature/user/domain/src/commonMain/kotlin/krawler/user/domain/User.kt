package krawler.user.domain

import krawler.core.domain.AggregateRoot
import krawler.core.domain.WithDomainEvent

/**
 * Aggregate root representing a user in the system.
 *
 * Encapsulates the user's identity, display name, and the collection of linked Brawl Stars players.
 * This aggregate enforces rules around managing linked players, ensuring consistency such as:
 * - Preventing duplicate player links in addition.
 * - Allowing explicit refresh of player data only if already linked.
 * - Safe removal of linked players by tag.
 *
 * All mutation-like operations return a [WithDomainEvent] containing a new [User]
 * instance reflecting the change, along with a domain event describing the update.
 *
 * Use the provided methods to update user information and linked players explicitly,
 * favoring clarity and explicit intent over implicit or "magical" state changes.
 *
 * @property id Unique system identifier for the user.
 * @property linkedPlayers Collection of Brawl Stars players linked to the user.
 * @property linkedTelegram Telegram account associated with the user.
 */
@AggregateRoot
public class User(
    public val id: UserId,
    public val linkedPlayers: LinkedBrawlStarsPlayers,
    public val linkedTelegram: LinkedTelegram,
) {
    /**
     * Returns a new [User] instance with updated [linkedTelegram],
     * paired with a [UserUpdateEvent.LinkedTelegramChanged] event.
     */
    public fun linkTelegram(
        telegram: LinkedTelegram,
    ): WithDomainEvent<User, UserUpdateEvent> {
        val updated = User(id, linkedPlayers, telegram)
        val event = UserUpdateEvent.LinkedTelegramChanged(id, telegram)
        return WithDomainEvent(updated, event)
    }

    /**
     * Returns a new [User] with the [linkedTelegram] transformed via [transform],
     * paired with the corresponding [UserUpdateEvent].
     */
    public inline fun linkTelegram(
        transform: (LinkedTelegram) -> LinkedTelegram,
    ): WithDomainEvent<User, UserUpdateEvent> = linkTelegram(transform(linkedTelegram))

    /**
     * Adds a new [player] to the user's linked players.
     *
     * @throws IllegalArgumentException if the player is already linked.
     *
     * Returns a new [User] and a [UserUpdateEvent.PlayerAdded] event.
     */
    public fun assignPlayer(
        player: BrawlStarsPlayer,
    ): WithDomainEvent<User, UserUpdateEvent> {
        val updated = User(id, linkedPlayers.assign(player), linkedTelegram)
        val event = UserUpdateEvent.PlayerAdded(id, player)
        return WithDomainEvent(updated, event)
    }

    /**
     * Refreshes data for an already linked [player].
     *
     * @throws IllegalArgumentException if the player is not currently linked.
     *
     * Returns a new [User] and a [UserUpdateEvent.PlayerRefreshed] event.
     *
     * @see LinkedBrawlStarsPlayers.update
     */
    @Throws(IllegalArgumentException::class)
    public fun updatePlayer(
        player: BrawlStarsPlayer,
    ): WithDomainEvent<User, UserUpdateEvent.PlayerRefreshed> {
        val updated = User(id, linkedPlayers.update(player), linkedTelegram)
        val event = UserUpdateEvent.PlayerRefreshed(id, player)
        return WithDomainEvent(updated, event)
    }

    /**
     * Removes a linked player by [tag].
     *
     * @return A [WithDomainEvent] containing the updated [User] with the player removed,
     *         and a [UserUpdateEvent.PlayerRemoved] event.
     *
     * If the player is not linked, returns the current user with a no-op event.
     */
    public fun unassignPlayer(
        tag: BrawlStarsPlayerTag,
    ): WithDomainEvent<User, UserUpdateEvent.PlayerRemoved?> {
        if (!linkedPlayers.has(tag)) {
            return WithDomainEvent(this, null)
        }
        val updated = User(id, linkedPlayers.unassign(tag), linkedTelegram)
        val event = UserUpdateEvent.PlayerRemoved(id, tag)
        return WithDomainEvent(updated, event)
    }

    /**
     * Removes all linked players from the user.
     *
     * Returns a new [User] with an empty [linkedPlayers] list,
     * paired with a [UserUpdateEvent.AllPlayersRemoved] event.
     */
    public fun unassignAllPlayers(): WithDomainEvent<User, UserUpdateEvent.AllPlayersRemoved> {
        val updated = User(id, linkedPlayers.unassignAll(), linkedTelegram)
        val event = UserUpdateEvent.AllPlayersRemoved(id)
        return WithDomainEvent(updated, event)
    }
}
