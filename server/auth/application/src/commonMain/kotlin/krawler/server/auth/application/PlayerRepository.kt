package krawler.server.auth.application

import krawler.server.auth.domain.ChallengedPlayerTag

/**
 * Provides access to player persistence.
 *
 * Allows checking whether a player with the given [ChallengedPlayerTag] exists.
 * The result indicates success or failure of the lookup operation.
 */
interface PlayerRepository {
    suspend fun exists(tag: ChallengedPlayerTag): Result<Boolean>
}
