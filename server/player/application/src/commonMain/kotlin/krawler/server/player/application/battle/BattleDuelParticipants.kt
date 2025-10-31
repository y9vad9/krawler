package krawler.server.player.application.battle

import krawler.server.player.application.PlayerTag

/**
 * Represents a collection of participants involved in a Brawl Stars battle.
 *
 * Implementations of this interface are specialized by game mode or battle type,
 * such as [FriendlyDuelBattleParticipants], [RankedBattlePlayers],
 * or [TrophyLeagueBattleDuelPlayers].
 *
 * > **Note**: Beware that Brawl Stars API may return only one brawler per player,
 * > instead of multiple. Adjust your application logic accordingly.
 */
sealed interface BattleDuelParticipants {
    /**
     * The list of individual participants in the battle.
     */
    val list: List<BattleDuelParticipant>

    /**
     * Returns the participant matching the specified [tag], or `null` if no match is found.
     *
     * @param tag The [PlayerTag] of the participant to locate.
     * @return The [BattleDuelParticipant] if present, or `null`.
     */
    fun getParticipantOrNull(tag: PlayerTag): BattleDuelParticipant?

    /**
     * Returns the participant matching the specified [tag], or throws if not found.
     *
     * @param tag The [PlayerTag] of the participant to locate.
     * @return The [BattleDuelParticipant] if found.
     * @throws IllegalStateException If no participant with the given tag exists.
     */
    @Throws(IllegalArgumentException::class)
    fun getParticipantOrThrow(tag: PlayerTag): BattleDuelParticipant

    /**
     * Returns the first participant of this duel.
     */
    val first: BattleDuelParticipant

    /**
     * Returns the second participant of this duel.
     */
    val second: BattleDuelParticipant
}

/**
 * Represents duel participants in a friendly battle (custom games or friendly matches).
 *
 * These battles do not affect trophies or rank progression.
 *
 * > **Note**: Beware that Brawl Stars API may return only one brawler per player,
 * > instead of multiple. Adjust your application logic accordingly.
 */
data class FriendlyDuelBattleParticipants(
    override val list: List<BattleDuelParticipant>,
) : BattleDuelParticipants {

    override fun getParticipantOrNull(tag: PlayerTag): BattleDuelParticipant? =
        list.firstOrNull { it.tag == tag }

    override fun getParticipantOrThrow(tag: PlayerTag): BattleDuelParticipant =
        getParticipantOrNull(tag) ?: throwUnableToFindParticipant(tag)

    /**
     * Returns the first participant of this duel.
     */
    override val first: BattleDuelParticipant
        get() = list[0]

    /**
     * Returns the second participant of this duel.
     */
    override val second: BattleDuelParticipant
        get() = list[1]
}

/**
 * Attempts to retrieve the [BattlePlayer] with the given [tag] from this team.
 *
 * > **Note**: Beware that Brawl Stars API may return only one brawler per player,
 * > instead of multiple. Adjust your application logic accordingly.
 *
 * @param tag The [PlayerTag] of the player to look for.
 * @return The matching [BattlePlayer], or `null` if not found or not a player.
 */
fun FriendlyDuelBattleParticipants.getAsPlayerOrNull(tag: PlayerTag): BattleDuelPlayer? =
    getParticipantOrNull(tag) as? BattleDuelPlayer?

/**
 * Retrieves the [BattlePlayer] with the given [tag] from this team.
 *
 * @param tag The [PlayerTag] of the player to look for.
 * @return The matching [BattlePlayer].
 * @throws ClassCastException if the participant exists but is not a player.
 * @throws IllegalStateException if no participant with the given tag exists.
 */
fun FriendlyDuelBattleParticipants.getAsPlayerOrThrow(tag: PlayerTag): BattleDuelPlayer =
    getParticipantOrThrow(tag) as BattleDuelPlayer

/**
 * Attempts to retrieve the [BattleBot] with the given [tag] from this team.
 *
 * @param tag The [PlayerTag] of the bot to look for.
 * @return The matching [BattleBot], or `null` if not found or not a bot.
 */
fun FriendlyDuelBattleParticipants.getAsBotOrNull(tag: PlayerTag): BattleDuelBot? =
    getParticipantOrNull(tag) as? BattleDuelBot?

/**
 * Retrieves the [BattleBot] with the given [tag] from this team.
 *
 * @param tag The [PlayerTag] of the bot to look for.
 * @return The matching [BattleBot].
 * @throws ClassCastException if the participant exists but is not a bot.
 * @throws IllegalStateException if no participant with the given tag exists.
 */
fun FriendlyDuelBattleParticipants.getAsBotOrThrow(tag: PlayerTag): BattleDuelBot =
    getParticipantOrThrow(tag) as BattleDuelBot

/**
 * Represents duel participants in a trophy league battle.
 *
 * These battles directly impact a player's trophy count and progression on the ladder.
 *
 * > **Note**: Beware that Brawl Stars API may return only one brawler per player,
 * > instead of multiple even if that is logical. Adjust your application logic accordingly.
 */
data class TrophyLeagueBattleDuelPlayers(
    override val list: List<TrophyLeagueBattleDuelPlayer>,
) : BattleDuelParticipants {
    override fun getParticipantOrNull(tag: PlayerTag): TrophyLeagueBattleDuelPlayer? =
        list.firstOrNull { it.tag == tag }

    override fun getParticipantOrThrow(tag: PlayerTag): TrophyLeagueBattleDuelPlayer =
        getParticipantOrNull(tag) ?: throwUnableToFindParticipant(tag)

    /**
     * Returns the first participant of this duel.
     */
    override val first: TrophyLeagueBattleDuelPlayer
        get() = list[0]

    /**
     * Returns the second participant of this duel.
     */
    override val second: TrophyLeagueBattleDuelPlayer
        get() = list[1]
}

private fun throwUnableToFindParticipant(tag: PlayerTag): Nothing =
    throw IllegalArgumentException("Unable to find participant with given tag: ${tag.stringWithTagPrefix}.")
