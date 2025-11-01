package krawler.server.player.application

/**
 * Represents a player that has been successfully authenticated.
 *
 * @property playerTag The unique tag identifying the authenticated player.
 */
data class AuthenticatedPlayer(
    val playerTag: PlayerTag,
)
