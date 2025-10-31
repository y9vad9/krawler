package krawler.server.player.application.battle

import kotlin.time.Instant
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Marker interface for all **duel**-style battles in Brawl Stars.
 *
 * A duel battle is a 1v1 format where each player brings multiple brawlers to compete
 * in a series of rounds. The battle is represented by its [participants], where
 * each participant’s brawler lineup and performance is recorded.
 *
 * This sealed interface is implemented by specific duel battle types such as:
 * - [ClassicFriendlyDuelBattle] — unranked duels in friendly rooms or friendly challenges.
 * - [TrophyLeagueDuelBattle] — ranked duels played during the trophy league season.
 *
 * > **Note**: The Brawl Stars API may sometimes return only one brawler per player,
 * > instead of multiple. Adjust your application logic accordingly.
 *
 * Common properties:
 * - [participants] — participants in the duel, including their brawler rosters.
 * - [result] — the outcome of the battle from the querying player’s perspective.
 */
sealed interface DuelBattle : SoloBattle {
    /**
     * The participants in this duel battle.
     * Different implementations may use different participant models
     * depending on whether the duel was friendly or part of trophy league.
     */
    val participants: BattleDuelParticipants

    /**
     * The result of the battle as seen by the querying player.
     */
    val result: BattleResult
}
/**
 * Marker interface for **friendly duel battles** — unranked 1v1 matches typically played in friendly rooms.
 *
 * Friendly duel battles include full battle details but do **not** affect trophies or rankings.
 * They always have two participants with their brawler rosters represented by [participants].
 */
sealed interface FriendlyDuelBattle : DuelBattle, FriendlyBattle {
    /** The two participants in the duel, including their brawler rosters. */
    override val participants: FriendlyDuelBattleParticipants
}

/**
 * Marker interface for **Map Maker duel battles** — duels played on community-created maps.
 *
 * These battles are unofficial and may follow different rules than standard duels.
 * They include battle metadata and two participants, but may be distinct from Friendly or Trophy League duels.
 */
sealed interface MapMakerDuelBattle : DuelBattle, MapMakerBattle

/**
 * A **friendly** duel battle — an unranked 1v1 match typically played in a friendly room.
 *
 * These battles are for fun or practice and do not affect trophies.
 * They still capture full duel details such as:
 * - Player identities
 * - Brawler lineups and results
 * - Event metadata ([event])
 * - Battle timestamp ([time])
 *
 * > **Note**: The Brawl Stars API may sometimes return only one brawler per player,
 * > instead of multiple. Adjust your application logic accordingly.
 *
 * @property participants The two participants and their brawler rosters.
 * @property time The timestamp when this battle occurred.
 * @property event The event (map and mode) in which this duel took place.
 * @property result The battle result for the querying player.
 */
data class ClassicFriendlyDuelBattle(
    override val participants: FriendlyDuelBattleParticipants,
    override val time: Instant,
    override val event: BattleEvent,
    override val result: BattleResult,
) : FriendlyDuelBattle

/**
 * Represents a **friendly duel battle** played on a community-created Map Maker map.
 *
 * These battles are unranked 1v1 matches and do not affect player trophies.
 * They capture full duel details such as:
 * - Player identities and brawler lineups ([participants])
 * - Event metadata ([event])
 * - Battle timestamp ([time])
 * - Battle result for the querying player ([result])
 *
 * > **Note**: The Brawl Stars API may sometimes return only one brawler per player,
 * > instead of multiple. Adjust your application logic accordingly.
 *
 * @property participants The two participants and their brawler rosters.
 * @property time The timestamp when this battle occurred.
 * @property event The Map Maker event in which this duel took place.
 * @property result The battle result for the querying player.
 */
data class FriendlyMapMakerDuelBattle(
    override val participants: FriendlyDuelBattleParticipants,
    override val time: Instant,
    override val event: MapMakerBattleEvent,
    override val result: BattleResult,
) : FriendlyDuelBattle, MapMakerDuelBattle

/**
 * Represents a **Map Maker duel battle** involving Trophy League players.
 *
 * These battles are intended for forward-compatibility and currently do not exist
 * in the game as global Map Maker events. They capture duel-specific information such as:
 * - Trophy League participants and their brawler lineups ([participants])
 * - Event metadata ([event])
 * - Battle timestamp ([time])
 * - Battle result for the querying player ([result])
 *
 * > **Note**: The Brawl Stars API may sometimes return only one brawler per player,
 * > instead of multiple. Adjust your application logic accordingly.
 *
 * @property participants The Trophy League players and their brawler rosters.
 * @property time The timestamp when this battle occurred.
 * @property event The Map Maker event in which this duel would take place.
 * @property result The battle result for the querying player.
 */
data class GlobalMapMakerDuelBattle(
    override val participants: TrophyLeagueBattleDuelPlayers,
    override val time: Instant,
    override val event: MapMakerBattleEvent,
    override val result: BattleResult,
) : MapMakerDuelBattle

/**
 * A **trophy league** duel battle — a ranked 1v1 match played during a trophy league season.
 *
 * These battles affect trophy counts and are part of the competitive ranked progression.
 * Trophy league duels typically involve matchmaking and higher stakes compared to friendly duels.
 *
 * > **Note**: The Brawl Stars API may sometimes return only one brawler per player,
 * > instead of multiple. Adjust your application logic accordingly.
 *
 * @property participants The two ranked participants with trophy league–specific stats.
 * @property time The timestamp when this battle occurred.
 * @property event The event (map and mode) in which this duel took place.
 * @property result The battle result for the querying player.
 */
data class TrophyLeagueDuelBattle(
    override val participants: TrophyLeagueBattleDuelPlayers,
    override val time: Instant,
    override val event: BattleEvent,
    override val result: BattleResult,
) : DuelBattle

/**
 * Returns `true` if this duel battle is a [ClassicFriendlyDuelBattle].
 *
 * This check is contract-aware, so inside an `if (isFriendly())` block
 * the compiler smart-casts `this` to [ClassicFriendlyDuelBattle].
 *
 * @receiver The duel battle instance to check.
 * @return `true` if the duel battle is friendly, otherwise `false`.
 */
@OptIn(ExperimentalContracts::class)
fun DuelBattle.isFriendly(): Boolean {
    contract {
        returns(true) implies (this@isFriendly is ClassicFriendlyDuelBattle)
    }
    return this is ClassicFriendlyDuelBattle
}

/**
 * Returns `true` if this duel battle is a [TrophyLeagueDuelBattle].
 *
 * This check is contract-aware, so inside an `if (isTrophyLeague())` block
 * the compiler smart-casts `this` to [TrophyLeagueDuelBattle].
 *
 * @receiver The duel battle instance to check.
 * @return `true` if the duel battle is a trophy league duel, otherwise `false`.
 */
@OptIn(ExperimentalContracts::class)
fun DuelBattle.isTrophyLeague(): Boolean {
    contract {
        returns(true) implies (this@isTrophyLeague is TrophyLeagueDuelBattle)
    }
    return this is TrophyLeagueDuelBattle
}
