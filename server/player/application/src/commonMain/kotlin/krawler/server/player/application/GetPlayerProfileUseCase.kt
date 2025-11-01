package krawler.server.player.application

import krawler.logger.Logger
import krawler.logger.withThrowable

/**
 * Use case for retrieving a player's profile with access control.
 *
 * Validates whether the `principal` is allowed to access the profile of the player
 * identified by `tag`. Access is permitted only if both players belong to the same club
 * or club alliance.
 *
 * @param repository Repository for fetching player data and performing access checks.
 * @param logger Logger used to report repository failures or access check issues.
 */
class GetPlayerProfileUseCase(
    private val repository: PlayerRepository,
    private val logger: Logger,
) {
    /**
     * Executes the player profile retrieval for the given [tag] on behalf of [principal].
     *
     * @param principal The player requesting the profile.
     * @param tag The tag of the player whose profile is being requested.
     * @return [Result] indicating success, access denial, or repository failure.
     */
    suspend fun execute(
        principal: AuthenticatedPlayer,
        tag: PlayerTag,
    ): Result {
        val requestedPlayer = repository.getPlayer(tag)
            .getOrElse { throwable ->
                logger
                    .withField("player_tag", tag.stringWithTagPrefix)
                    .withThrowable(throwable)
                    .warning("Unable to retrieve player from repository")
                return Result.Failure
            }
            ?: return Result.NotFound

        val isPermitted = principal.playerTag == tag ||
                repository.isWithinSameClubOrClubAlliance(principal.playerTag, tag)
                    .getOrElse { throwable ->
                        logger
                            .withField("player_tag", tag.stringWithTagPrefix)
                            .withThrowable(throwable)
                            .warning("Unable to check whether two players are from the same club/alliance")
                        return Result.Failure
                    }

        return if (isPermitted) Result.Success(requestedPlayer) else Result.PermissionDenied
    }

    /** Outcome of executing [GetPlayerProfileUseCase]. */
    sealed interface Result {
        /** Access to the requested player's profile is denied. */
        data object PermissionDenied : Result

        /** Player with the given tag was not found. */
        data object NotFound : Result

        /** Successfully retrieved player profile. */
        data class Success(val player: Player) : Result

        /** Failure during repository or access check operations. */
        data object Failure : Result
    }
}
