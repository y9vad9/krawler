package com.y9vad9.brawlex.user.domain.value

import com.y9vad9.brawlex.user.domain.LinkedPlayer
import com.y9vad9.brawlex.user.domain.User
import com.y9vad9.valdi.domain.ValueObject
import kotlin.jvm.JvmInline
import kotlin.jvm.Throws

/**
 * Represents a list of [LinkedPlayer]s associated with a [User].
 *
 * This value object captures the state of player linking, allowing the system
 * to reason about whether a user has no linked players, one, or multiple.
 */
/**
 * Represents a list of [LinkedPlayer]s associated with a [User].
 *
 * This value object captures the state of player linking, allowing the system
 * to reason about whether a user has no linked players, one, or multiple.
 */
@ValueObject
@JvmInline
public value class LinkedPlayers(
    public val list: List<LinkedPlayer>,
) {
    /** True when the user has linked more than one player. */
    public val isLinkedToMultiple: Boolean get() = list.size > 1

    /** True when the user has linked exactly one player. */
    public val isLinkedToSingle: Boolean get() = list.size == 1

    /** True when the user hasn't linked any players. */
    public val isNotLinked: Boolean get() = list.isEmpty()

    /** True if player with specified [tag] is already linked */
    public fun has(tag: LinkedPlayerTag): Boolean {
        return list.any { it.tag == tag }
    }

    /** Returns the linked player with the given [tag], or `null` if not found. */
    public operator fun get(tag: LinkedPlayerTag): LinkedPlayer? =
        list.firstOrNull { it.tag == tag }

    /**
     * Adds a new player. Throws [IllegalArgumentException] if player is already linked.
     */
    public fun with(player: LinkedPlayer): LinkedPlayers {
        require(!has(player.tag)) { "Player with tag ${player.tag} is already linked." }
        return LinkedPlayers(list + player)
    }

    /**
     * Refreshes (updates) an existing player. Throws [IllegalArgumentException] if player is not linked.
     */
    @Throws(IllegalArgumentException::class)
    public fun withRefreshed(player: LinkedPlayer): LinkedPlayers {
        require(has(player.tag)) { "Player with tag ${player.tag} is not linked." }
        return LinkedPlayers(list.map { if (it.tag == player.tag) player else it })
    }

    /** Removes a linked player by [tag]. Returns same instance if not found. */
    public fun without(tag: LinkedPlayerTag): LinkedPlayers {
        if (!has(tag)) return this
        return LinkedPlayers(list.filterNot { it.tag == tag })
    }

    /** Replaces all players with [newList]. */
    public fun replacedWith(newList: List<LinkedPlayer>): LinkedPlayers =
        LinkedPlayers(newList)

    /** Removes all linked players. */
    public fun withoutAll(): LinkedPlayers = LinkedPlayers(emptyList())

    override fun toString(): String {
        val playersInfo = list.joinToString(separator = ", ") { "${it.name} (${it.tag})" }
        return "LinkedPlayers(count=${list.size}, players=[$playersInfo])"
    }
}
