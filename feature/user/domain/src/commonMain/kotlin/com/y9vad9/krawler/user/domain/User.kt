package com.y9vad9.krawler.user.domain

import com.y9vad9.krawler.core.domain.WithDomainUpdateEvent
import com.y9vad9.krawler.user.domain.entity.BrawlStarsPlayer
import com.y9vad9.krawler.user.domain.entity.LinkedTelegram
import com.y9vad9.krawler.user.domain.event.UserUpdateEvent
import com.y9vad9.krawler.user.domain.value.BrawlStarsPlayerTag
import com.y9vad9.krawler.user.domain.value.LinkedBrawlStarsPlayers
import com.y9vad9.krawler.user.domain.value.UserId
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
 * All mutation-like operations return a [WithDomainUpdateEvent] containing a new [User]
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
    public fun withNewLinkedTelegram(
        telegram: LinkedTelegram,
    ): WithDomainUpdateEvent<User, UserUpdateEvent> {
        val updated = User(id, linkedPlayers, telegram)
        val event = UserUpdateEvent.LinkedTelegramChanged(id, telegram)
        return WithDomainUpdateEvent(updated, event)
    }

    /**
     * Returns a new [User] with the [linkedTelegram] transformed via [transform],
     * paired with the corresponding [UserUpdateEvent].
     */
    public fun withUpdatedLinkedTelegram(
        transform: (LinkedTelegram) -> LinkedTelegram,
    ): WithDomainUpdateEvent<User, UserUpdateEvent> = withNewLinkedTelegram(transform(linkedTelegram))

    /**
     * Adds a new [player] to the user's linked players.
     *
     * @throws IllegalArgumentException if the player is already linked.
     *
     * Returns a new [User] and a [UserUpdateEvent.PlayerAdded] event.
     */
    public fun withAddedPlayer(
        player: BrawlStarsPlayer,
    ): WithDomainUpdateEvent<User, UserUpdateEvent> {
        val updated = User(id, linkedPlayers.withNew(player), linkedTelegram)
        val event = UserUpdateEvent.PlayerAdded(id, player)
        return WithDomainUpdateEvent(updated, event)
    }

    /**
     * Refreshes data for an already linked [player].
     *
     * @throws IllegalArgumentException if the player is not currently linked.
     *
     * Returns a new [User] and a [UserUpdateEvent.PlayerRefreshed] event.
     *
     * @see LinkedBrawlStarsPlayers.withRefreshed
     */
    @Throws(IllegalArgumentException::class)
    public fun withRefreshedPlayer(
        player: BrawlStarsPlayer,
    ): WithDomainUpdateEvent<User, UserUpdateEvent.PlayerRefreshed> {
        val updated = User(id, linkedPlayers.withRefreshed(player), linkedTelegram)
        val event = UserUpdateEvent.PlayerRefreshed(id, player)
        return WithDomainUpdateEvent(updated, event)
    }

    /**
     * Removes a linked player by [tag].
     *
     * @return A [WithDomainUpdateEvent] containing the updated [User] with the player removed,
     *         and a [UserUpdateEvent.PlayerRemoved] event.
     *
     * If the player is not linked, returns the current user with a no-op event.
     */
    public fun withoutLinkedPlayer(
        tag: BrawlStarsPlayerTag,
    ): WithDomainUpdateEvent<User, UserUpdateEvent.PlayerRemoved?> {
        if (!linkedPlayers.has(tag)) {
            return WithDomainUpdateEvent(this, null)
        }
        val updated = User(id, linkedPlayers.without(tag), linkedTelegram)
        val event = UserUpdateEvent.PlayerRemoved(id, tag)
        return WithDomainUpdateEvent(updated, event)
    }

    /**
     * Removes all linked players from the user.
     *
     * Returns a new [User] with an empty [linkedPlayers] list,
     * paired with a [UserUpdateEvent.AllPlayersRemoved] event.
     */
    public fun withoutAllLinkedPlayers(): WithDomainUpdateEvent<User, UserUpdateEvent.AllPlayersRemoved> {
        val updated = User(id, linkedPlayers.withoutAll(), linkedTelegram)
        val event = UserUpdateEvent.AllPlayersRemoved(id)
        return WithDomainUpdateEvent(updated, event)
    }
}
