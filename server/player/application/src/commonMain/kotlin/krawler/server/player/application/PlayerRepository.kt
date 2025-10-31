package krawler.server.player.application

import krawler.server.player.application.battle.Battle

/**
 * Repository interface for retrieving player-related information.
 *
 * Provides methods to fetch basic player data, battle history, and owned brawlers.
 */
interface PlayerRepository {

    /**
     * Retrieves a player by their [tag].
     *
     * @param tag Unique identifier of the player.
     * @return [Result] wrapping the [Player] if found, or `null` if no player exists with the given tag.
     */
    suspend fun getPlayer(tag: PlayerTag): Result<Player?>

    /**
     * Returns whether the given players are from the same club or club alliance.
     *
     * @param firstPlayer Unique identifier of the first player.
     * @param secondPlayer Unique identifier of the second player.
     * @return [Result] wrapping the [Boolean] stating whether two players are from the same club or club alliance.
     */
    suspend fun isWithinSameClubOrClubAlliance(firstPlayer: PlayerTag, secondPlayer: PlayerTag): Result<Boolean>

    /**
     * Retrieves the battle log of a player identified by [tag].
     *
     * @param tag Unique identifier of the player.
     * @param cursor Current position in the pagination. Starts from the very beginning if null.
     * @return [Result] containing a list of [Battle]s, or an error if the operation fails.
     */
    suspend fun getPlayerBattleLog(tag: PlayerTag, cursor: PaginationCursor?): Result<BattlesWithCursor>

    /**
     * Retrieves the list of brawlers owned by the player identified by [tag].
     *
     * @param tag Unique identifier of the player.
     * @return [Result] containing a list of [PlayerBrawler]s, or an error if the operation fails.
     */
    suspend fun getPlayerBrawlers(tag: PlayerTag): Result<List<PlayerBrawler>>

    /**
     * Represents a paginated segment of player battles.
     *
     * @property previousCursor Cursor pointing to the previous page of results, or `null` if none exists.
     * @property nextCursor Cursor pointing to the next page of results, or `null` if none exists.
     * @property battles List of battles retrieved for the current page.
     */
    data class BattlesWithCursor(
        val previousCursor: PaginationCursor?,
        val nextCursor: PaginationCursor?,
        val battles: List<Battle>,
    )
}
