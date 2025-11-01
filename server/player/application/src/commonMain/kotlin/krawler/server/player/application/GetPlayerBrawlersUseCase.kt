package krawler.server.player.application

import krawler.logger.Logger
import krawler.logger.withThrowable

/**
 * Retrieves brawler data for a given player while enforcing access control rules.
 *
 * Access is permitted if the authenticated player matches the requested player
 * or if both belong to the same club or club alliance.
 *
 * Logs repository or permission check failures, but does not propagate exceptions.
 */
class GetPlayerBrawlersUseCase(
    private val repository: PlayerRepository,
    private val logger: Logger,
) {
    /**
     * Executes the retrieval of brawlers for the specified player.
     *
     * @param principal Authenticated player making the request.
     * @param tag Tag of the player whose brawlers are being requested.
     * @return [Result.Success] if brawlers were retrieved successfully,
     * [Result.PermissionDenied] if access is restricted,
     * [Result.PlayerNotFound] if the player has no brawlers recorded,
     * or [Result.Failure] if a repository or access check failed.
     */
    suspend fun execute(principal: AuthenticatedPlayer, tag: PlayerTag): Result {
        val isPermitted = principal.playerTag == tag ||
                repository.isWithinSameClubOrClubAlliance(principal.playerTag, tag)
                    .getOrElse { throwable ->
                        logger
                            .withField("player_tag", tag.stringWithTagPrefix)
                            .withThrowable(throwable)
                            .warning("Unable to check whether two players are from the same club/alliance")
                        return Result.Failure
                    }

        return if (isPermitted) {
            Result.Success(
                repository.getPlayerBrawlers(tag)
                    .getOrElse { throwable ->
                        logger
                            .withField("player_tag", tag.stringWithTagPrefix)
                            .withThrowable(throwable)
                            .warning("Unable to retrieve player brawlers from repository")
                        return Result.Failure
                    }
                    .takeUnless { it.isEmpty() }
                    ?: return Result.PlayerNotFound
            )
        } else {
            Result.PermissionDenied
        }
    }

    /**
     * Represents possible outcomes of executing [GetPlayerBrawlersUseCase].
     */
    sealed interface Result {
        /**
         * Brawlers retrieved successfully.
         *
         * @property brawlers The list of retrieved brawlers.
         */
        data class Success(val brawlers: List<PlayerBrawler>) : Result

        /** Access denied due to permission restrictions. */
        data object PermissionDenied : Result

        /** The player has no recorded brawlers or was not found. */
        data object PlayerNotFound : Result

        /** Repository or access check failure occurred. */
        data object Failure : Result
    }
}
