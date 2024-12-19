@file:OptIn(ValidationDelicateApi::class)

package com.y9vad9.starix.core.brawlstars.entity.event

import com.y9vad9.bcm.core.brawlstars.entity.brawler.value.BrawlerId
import com.y9vad9.bcm.core.brawlstars.entity.brawler.value.BrawlerName
import com.y9vad9.bcm.core.brawlstars.entity.brawler.value.PowerLevel
import com.y9vad9.bcm.core.brawlstars.entity.event.value.*
import com.y9vad9.bcm.core.brawlstars.entity.player.value.PlayerName
import com.y9vad9.bcm.core.brawlstars.entity.player.value.PlayerTag
import com.y9vad9.starix.foundation.validation.annotations.ValidationDelicateApi
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Battle(
    val battleTime: Instant,
    val event: Event,
    val mode: EventMode,
    val duration: Seconds,
    val result: BattleResult,
    val type: BattleType,
    val starPlayer: PlayerView,
    val teams: BattleTeams,
) {
    @Serializable
    data class PlayerView(
        val tag: PlayerTag,
        val name: PlayerName,
        val brawler: BrawlerBattleView,
    )


    @Serializable
    data class BrawlerBattleView(
        val id: BrawlerId,
        val name: BrawlerName,
        val power: PowerLevel,
        /**
         * Contains meaningful information only if [Battle.type] is for trophies.
         */
        val trophies: Trophies,
    )
}