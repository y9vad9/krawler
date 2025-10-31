package krawler.server.player.application

import krawler.logger.Logger
import krawler.logger.withThrowable
import krawler.server.player.application.battle.Battle

/**
 * Retrieves a paginated list of player battles with access control.
 *
 * The use case ensures that only the authenticated player or members
 * of the same club or club alliance can access the target player's battle log.
 * Results are returned in a paginated format using [PaginationCursor].
 */
class GetPlayerBattlelogUseCase(
    private val repository: PlayerRepository,
    private val logger: Logger,
) {

    /**
     * Executes the retrieval of the player’s battle log.
     *
     * @param principal Authenticated player requesting the data.
     * @param tag Tag of the player whose battle log is requested.
     * @param cursor Optional pagination cursor for retrieving a specific page.
     * @return [Result] indicating success with data, permission denial, or failure.
     */
    suspend fun execute(
        principal: AuthenticatedPlayer,
        tag: PlayerTag,
        cursor: PaginationCursor?,
    ): Result {
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
            val result = repository.getPlayerBattleLog(tag, cursor)
                .getOrElse { throwable ->
                    logger
                        .withField("player_tag", tag.stringWithTagPrefix)
                        .withThrowable(throwable)
                        .warning("Unable to retrieve player battles from repository")
                    return Result.Failure
                }

            Result.Success(
                battles = result.battles,
                previousCursor = result.previousCursor,
                nextCursor = result.nextCursor,
            )
        } else {
            Result.PermissionDenied
        }
    }

    /**
     * Represents possible outcomes of the battle log retrieval.
     */
    sealed interface Result {
        /** Successfully retrieved battles for the current page. */
        data class Success(
            val battles: List<Battle>,
            val previousCursor: PaginationCursor?,
            val nextCursor: PaginationCursor?,
        ) : Result

        /** Player was not found. */
        data object PlayerNotFound : Result

        /** Requester does not have permission to view the battle log. */
        data object PermissionDenied : Result

        /** Unexpected repository or infrastructure error occurred. */
        data object Failure : Result
    }
}
