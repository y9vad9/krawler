package krawler.server.player.application.battle

import krawler.server.player.application.PlayerTag

/**
 * Represents a set of teams involved in a battle, each with their own participants.
 *
 * In most cases, this will represent 1v1, 3v3, or team-based formats where multiple teams
 * compete. Each team is represented by a [BattleParticipants] instance.
 */
sealed interface BattleTeamsParticipants {

    /**
     * List of [BattleParticipants] grouped by team.
     */
    val list: List<BattleParticipants>

    /**
     * Returns the [BattleParticipants] at the given [index], or `null` if the index is out of bounds.
     *
     * @param index The index of the team to retrieve (0-based).
     * @return The team at the given index, or `null`.
     */
    operator fun get(index: Int): BattleParticipants?

    /**
     * Returns the [BattleParticipants] at the given [index], or `null` if the index is out of bounds.
     *
     * @param index The index of the team to retrieve (0-based).
     * @return The team at the given index, or `null`.
     */
    fun getOrNull(index: Int): BattleParticipants?

    /**
     * Returns the [BattleParticipants] at the given [index], or throws an exception if the index is invalid.
     *
     * @param index The index of the team to retrieve (0-based).
     * @return The team at the given index.
     * @throws IndexOutOfBoundsException if no team exists at the given index.
     */
    fun getOrThrow(index: Int): BattleParticipants

    /**
     * Searches for a [BattleParticipant] with the given [PlayerTag] across all teams.
     *
     * @param tag The tag of the participant to find.
     * @return The participant if found, or `null`.
     */
    fun findParticipantOrNull(tag: PlayerTag): BattleParticipant?

    /**
     * Searches for a [BattleParticipant] with the given [PlayerTag] across all teams.
     *
     * @param tag The tag of the participant to find.
     * @return The participant if found.
     * @throws IllegalStateException if no participant with the given tag exists.
     */
    fun findParticipantOrThrow(tag: PlayerTag): BattleParticipant

    /**
     * Finds the team to which the given [PlayerTag] belongs.
     *
     * @param tag The tag of the participant.
     * @return The corresponding team, or `null` if not found.
     */
    fun getTeamOrNull(tag: PlayerTag): BattleParticipants?

    /**
     * Finds the team to which the given [PlayerTag] belongs.
     *
     * @param tag The tag of the participant.
     * @return The team that includes the given participant.
     * @throws IllegalStateException if no matching team exists.
     */
    fun getTeamOrThrow(tag: PlayerTag): BattleParticipants
}

/**
 * Returns the first team in the battle. Should never throw.
 *
 * @return The team at index 0.
 */
fun BattleTeamsParticipants.firstTeam(): BattleParticipants = getOrThrow(0)

/**
 * Returns the second team in the battle, or `null` if it doesn't exist.
 */
fun BattleTeamsParticipants.secondTeamOrNull(): BattleParticipants? = getOrNull(1)

/**
 * Returns the second team in the battle.
 *
 * @return The team at index 1.
 * @throws IndexOutOfBoundsException if no team exists at index 1.
 */
fun BattleTeamsParticipants.secondTeamOrThrow(): BattleParticipants = getOrThrow(1)

/**
 * Returns the third team in the battle, or `null` if it doesn't exist.
 */
@Suppress("detekt.MagicNumber")
fun BattleTeamsParticipants.thirdTeamOrNull(): BattleParticipants? = getOrNull(2)

/**
 * Returns the third team in the battle.
 *
 * @return The team at index 2.
 * @throws IndexOutOfBoundsException if no team exists at index 2.
 */
@Suppress("detekt.MagicNumber")
fun BattleTeamsParticipants.thirdTeamOrThrow(): BattleParticipants = getOrThrow(2)

/**
 * Returns the fourth team in the battle, or `null` if it doesn't exist.
 */
@Suppress("detekt.MagicNumber")
fun BattleTeamsParticipants.fourthTeamOrNull(): BattleParticipants? = getOrNull(3)

/**
 * Returns the fourth team in the battle.
 *
 * @return The team at index 3.
 * @throws IndexOutOfBoundsException if no team exists at index 3.
 */
@Suppress("detekt.MagicNumber")
fun BattleTeamsParticipants.fourthTeamOrThrow(): BattleParticipants = getOrThrow(3)

/**
 * Returns the fifth team in the battle, or `null` if it doesn't exist.
 */
@Suppress("detekt.MagicNumber")
fun BattleTeamsParticipants.fifthTeamOrNull(): BattleParticipants? = getOrNull(4)

/**
 * Returns the fifth team in the battle.
 *
 * @return The team at index 4.
 * @throws IndexOutOfBoundsException if no team exists at index 4.
 */
@Suppress("detekt.MagicNumber")
fun BattleTeamsParticipants.fifthTeamOrThrow(): BattleParticipants = getOrThrow(4)

/**
 * Represents teams in a friendly battle, each containing [FriendlyBattleParticipants].
 *
 * This implementation of [BattleTeamsParticipants] is specific to friendly matches,
 * where each team is composed of general [BattleParticipant] instances.
 *
 * @property list The list of teams participating in the battle.
 */
data class FriendlyBattleTeamsParticipants(
    override val list: List<FriendlyBattleParticipants>,
) : BattleTeamsParticipants {

    /**
     * Returns the team at the specified [index], or `null` if it does not exist.
     *
     * @param index The team index to retrieve.
     * @return The [FriendlyBattleParticipants] at the given index, or `null`.
     */
    override fun get(index: Int): FriendlyBattleParticipants? =
        getOrNull(index)

    /**
     * Returns the team at the specified [index], or `null` if it does not exist.
     *
     * @param index The team index to retrieve.
     * @return The [FriendlyBattleParticipants] at the given index, or `null`.
     */
    override fun getOrNull(index: Int): FriendlyBattleParticipants? =
        list.getOrNull(index)

    /**
     * Returns the team at the specified [index], or throws an exception if the index is invalid.
     *
     * @param index The team index to retrieve.
     * @return The [FriendlyBattleParticipants] at the given index.
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     */
    override fun getOrThrow(index: Int): FriendlyBattleParticipants =
        list[index]

    /**
     * Searches all teams for a participant with the given [tag].
     *
     * @param tag The tag of the participant to find.
     * @return The matching [BattleParticipant], or `null` if not found.
     */
    override fun findParticipantOrNull(tag: PlayerTag): BattleParticipant? =
        list.flatMap { it.list }.firstOrNull { it.tag == tag }

    /**
     * Searches all teams for a participant with the given [tag].
     *
     * @param tag The tag of the participant to find.
     * @return The matching [BattleParticipant].
     * @throws IllegalStateException if no participant with the given tag exists.
     */
    override fun findParticipantOrThrow(tag: PlayerTag): BattleParticipant =
        findParticipantOrNull(tag) ?: error(
            "Unable to find participant with given tag: ${tag.stringWithTagPrefix}"
        )

    /**
     * Finds the team containing a participant with the given [tag].
     *
     * @param tag The tag of the participant.
     * @return The corresponding team, or `null` if not found.
     */
    override fun getTeamOrNull(tag: PlayerTag): FriendlyBattleParticipants? =
        list.firstOrNull { participants ->
            participants.list.any { it.tag == tag }
        }

    /**
     * Finds the team containing a participant with the given [tag].
     *
     * @param tag The tag of the participant.
     * @return The corresponding [FriendlyBattleParticipants].
     * @throws IllegalStateException if no matching team exists.
     */
    override fun getTeamOrThrow(tag: PlayerTag): FriendlyBattleParticipants =
        getTeamOrNull(tag) ?: throwUnableToFindParticipantTeam(tag)
}

/**
 * Attempts to find a [BattlePlayer] in any friendly team by the given [tag].
 *
 * @param tag The [PlayerTag] of the player to find.
 * @return The matching [BattlePlayer], or `null` if not found or if the participant is not a player.
 */
fun FriendlyBattleTeamsParticipants.findPlayerOrNull(tag: PlayerTag): BattlePlayer? =
    findParticipantOrNull(tag) as? BattlePlayer?

/**
 * Finds a [BattlePlayer] in any friendly team by the given [tag].
 *
 * @param tag The [PlayerTag] of the player to find.
 * @return The matching [BattlePlayer].
 * @throws ClassCastException if the participant exists but is not a player.
 * @throws IllegalStateException if no participant with the given tag exists.
 */
fun FriendlyBattleTeamsParticipants.findPlayerOrThrow(tag: PlayerTag): BattlePlayer =
    findParticipantOrThrow(tag) as BattlePlayer

/**
 * Attempts to find a [BattleBot] in any friendly team by the given [tag].
 *
 * @param tag The [PlayerTag] of the bot to find.
 * @return The matching [BattleBot], or `null` if not found or if the participant is not a bot.
 */
fun FriendlyBattleTeamsParticipants.findBotOrNull(tag: PlayerTag): BattleBot? =
    findParticipantOrNull(tag) as? BattleBot?

/**
 * Finds a [BattleBot] in any friendly team by the given [tag].
 *
 * @param tag The [PlayerTag] of the bot to find.
 * @return The matching [BattleBot].
 * @throws ClassCastException if the participant exists but is not a bot.
 * @throws IllegalStateException if no participant with the given tag exists.
 */
fun FriendlyBattleTeamsParticipants.findBotOrThrow(tag: PlayerTag): BattleBot =
    findParticipantOrThrow(tag) as BattleBot

/**
 * Represents teams in a ranked game mode battle, where each team is composed of [RankedBattlePlayers].
 *
 * This implementation of [BattleTeamsParticipants] models the participants grouped into teams
 * for competitive ranked matches, typically featuring structured matchmaking and progression.
 *
 * @property list The list of ranked teams participating in the battle.
 */
data class RankedBattleTeamsPlayers(
    override val list: List<RankedBattlePlayers>,
) : BattleTeamsParticipants {

    /**
     * Returns the team at the specified [index], or `null` if it does not exist.
     *
     * @param index The team index to retrieve.
     * @return The [RankedBattlePlayers] at the given index, or `null`.
     */
    override fun get(index: Int): RankedBattlePlayers? =
        getOrNull(index)

    /**
     * Returns the team at the specified [index], or `null` if it does not exist.
     *
     * @param index The team index to retrieve.
     * @return The [RankedBattlePlayers] at the given index, or `null`.
     */
    override fun getOrNull(index: Int): RankedBattlePlayers? =
        list.getOrNull(index)

    /**
     * Returns the team at the specified [index], or throws an exception if the index is invalid.
     *
     * @param index The team index to retrieve.
     * @return The [RankedBattlePlayers] at the given index.
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     */
    override fun getOrThrow(index: Int): RankedBattlePlayers =
        list[index]

    /**
     * Searches all teams for a ranked participant with the given [tag].
     *
     * @param tag The tag of the participant to find.
     * @return The matching [RankedBattlePlayer], or `null` if not found.
     */
    override fun findParticipantOrNull(tag: PlayerTag): RankedBattlePlayer? =
        list.flatMap { it.list }.firstOrNull { it.tag == tag }

    /**
     * Searches all teams for a ranked participant with the given [tag].
     *
     * @param tag The tag of the participant to find.
     * @return The matching [RankedBattlePlayer].
     * @throws IllegalStateException if no participant with the given tag exists.
     */
    override fun findParticipantOrThrow(tag: PlayerTag): RankedBattlePlayer =
        findParticipantOrNull(tag) ?: error(
            "Unable to find participant with given tag: ${tag.stringWithTagPrefix}"
        )

    /**
     * Finds the team containing a ranked participant with the given [tag].
     *
     * @param tag The tag of the participant.
     * @return The corresponding team, or `null` if not found.
     */
    override fun getTeamOrNull(tag: PlayerTag): RankedBattlePlayers? =
        list.firstOrNull { participants ->
            participants.list.any { it.tag == tag }
        }

    /**
     * Finds the team containing a ranked participant with the given [tag].
     *
     * @param tag The tag of the participant.
     * @return The corresponding [RankedBattlePlayers].
     * @throws IllegalStateException if no matching team exists.
     */
    override fun getTeamOrThrow(tag: PlayerTag): RankedBattlePlayers =
        getTeamOrNull(tag) ?: throwUnableToFindParticipantTeam(tag)
}

/**
 * Represents teams in a trophy league battle, where each team is composed of [TrophyLeagueBattlePlayers].
 *
 * This implementation of [BattleTeamsParticipants] models participants grouped into teams for
 * casual or progression-based matches in the trophy league game mode, where players gain or lose trophies.
 *
 * @property list The list of trophy league teams participating in the battle.
 */
data class TrophyLeagueBattleTeamsPlayers(
    override val list: List<TrophyLeagueBattlePlayers>,
) : BattleTeamsParticipants {

    /**
     * Returns the team at the specified [index], or `null` if it does not exist.
     *
     * @param index The team index to retrieve.
     * @return The [TrophyLeagueBattlePlayers] at the given index, or `null`.
     */
    override fun get(index: Int): TrophyLeagueBattlePlayers? =
        getOrNull(index)

    /**
     * Returns the team at the specified [index], or `null` if it does not exist.
     *
     * @param index The team index to retrieve.
     * @return The [TrophyLeagueBattlePlayers] at the given index, or `null`.
     */
    override fun getOrNull(index: Int): TrophyLeagueBattlePlayers? =
        list.getOrNull(index)

    /**
     * Returns the team at the specified [index], or throws an exception if the index is invalid.
     *
     * @param index The team index to retrieve.
     * @return The [TrophyLeagueBattlePlayers] at the given index.
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     */
    override fun getOrThrow(index: Int): TrophyLeagueBattlePlayers =
        list[index]

    /**
     * Searches all teams for a trophy league participant with the given [tag].
     *
     * @param tag The tag of the participant to find.
     * @return The matching [TrophyLeagueBattlePlayer], or `null` if not found.
     */
    override fun findParticipantOrNull(tag: PlayerTag): TrophyLeagueBattlePlayer? =
        list.flatMap { it.list }.firstOrNull { it.tag == tag }

    /**
     * Searches all teams for a trophy league participant with the given [tag].
     *
     * @param tag The tag of the participant to find.
     * @return The matching [TrophyLeagueBattlePlayer].
     * @throws IllegalStateException if no participant with the given tag exists.
     */
    override fun findParticipantOrThrow(tag: PlayerTag): TrophyLeagueBattlePlayer =
        findParticipantOrNull(tag) ?: throwUnableToFindParticipantTeam(tag)

    /**
     * Finds the team containing a trophy league participant with the given [tag].
     *
     * @param tag The tag of the participant.
     * @return The corresponding team, or `null` if not found.
     */
    override fun getTeamOrNull(tag: PlayerTag): TrophyLeagueBattlePlayers? =
        list.firstOrNull { participants ->
            participants.list.any { it.tag == tag }
        }

    /**
     * Finds the team containing a trophy league participant with the given [tag].
     *
     * @param tag The tag of the participant.
     * @return The corresponding [TrophyLeagueBattlePlayers].
     * @throws IllegalStateException if no matching team exists.
     */
    override fun getTeamOrThrow(tag: PlayerTag): TrophyLeagueBattlePlayers =
        getTeamOrNull(tag) ?: throwUnableToFindParticipantTeam(tag)
}

private fun throwUnableToFindParticipantTeam(tag: PlayerTag): Nothing =
    error("Unable to find a team of participant with given tag: ${tag.stringWithTagPrefix}")
