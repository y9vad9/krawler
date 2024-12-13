package com.y9vad9.bcm.core.brawlstars.entity.event.value

import com.y9vad9.bcm.foundation.validation.ValueConstructor
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class BattleType private constructor(val value: String) {

    companion object : ValueConstructor<BattleType, String> {
        override val displayName: String = "BattleType"

        // Note: in API, regular matches for trophies are named as 'ranked'
        // that conflicts with 'RANKED' game mode.
        val TROPHIES: BattleType = BattleType("ranked")

        val SOLO_RANKED: BattleType = BattleType("soloRanked")
        val DUO_RANKED: BattleType = BattleType("duoRanked")
        val TRIO_RANKED: BattleType = BattleType("trioRanked")

        override fun create(value: String): Result<BattleType> {
            return Result.success(BattleType(value))
        }
    }
}