package krawler.server.brawlstars.client.battle

import kotlinx.serialization.Serializable

/**
 * Represents the actual battle payload returned by the API.
 *
 * This type is intentionally permissive to cover all observed battle data shapes, including:
 * - Team-based battles that use `teams: List<List<BattleParticipant>>`.
 * - Showdown-style battles represented by a flat list of `players: List<BattleParticipant>`.
 * - Placement-based battles which may include properties like `rank` and `trophyChange`.
 *
 * All fields are nullable and have sensible defaults to allow reuse of the same model
 * across various battle variants without strict assumptions.
 *
 * @property mode The game mode of the battle, if known.
 * @property type The type of the battle, if provided.
 * @property result The result of the battle (e.g., win, loss, draw), if available.
 * @property duration Duration of the battle in seconds, if available.
 * @property rank The ranking position of the player in the battle, present in ranking-based battles.
 * @property trophyChange The trophy gain or loss after the battle, present in trophy league battles.
 * @property starPlayer The participant designated as the star player of the battle, if any.
 * @property teams In team-based battles, a list of teams, where each team is a list of participants.
 * @property players In showdown-style battles, a flat list of participants.
 * @property level The battle level object, present in some cooperative battles such as Last Stand.
 */
@Serializable
data class RawBattleDetails(
    val mode: String? = null,
    val type: String? = null,
    val result: String? = null,
    val duration: Int? = null,
    val rank: Int? = null,
    val trophyChange: Int? = null,
    val starPlayer: RawBattleParticipant? = null,
    val teams: List<List<RawBattleParticipant>>? = null,
    val players: List<RawBattleParticipant>? = null,
    val level: RawBotsBattleLevel? = null,
)
