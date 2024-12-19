package com.y9vad9.starix.core.brawlstars.entity.event.value

import com.y9vad9.bcm.core.brawlstars.entity.event.Battle
import com.y9vad9.starix.foundation.validation.CreationFailure
import com.y9vad9.starix.foundation.validation.ValueConstructor
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class BattleTeams(private val value: List<List<Battle.PlayerView>>) {
    val first: List<Battle.PlayerView> get() = value[0]
    val second: List<Battle.PlayerView> get() = value[1]

    companion object : ValueConstructor<BattleTeams, List<List<Battle.PlayerView>>> {
        override val displayName: String = "BattleTeams"

        override fun create(value: List<List<Battle.PlayerView>>): Result<BattleTeams> {
            return when (value.size) {
                2 -> Result.success(BattleTeams(value))
                else -> Result.failure(CreationFailure.ofSizeExact(2))
            }
        }
    }
}