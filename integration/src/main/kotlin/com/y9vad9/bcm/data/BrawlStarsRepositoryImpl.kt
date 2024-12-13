@file:OptIn(ValidationDelicateApi::class)

package com.y9vad9.bcm.data

import com.y9vad9.bcm.core.brawlstars.entity.club.Club
import com.y9vad9.bcm.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.bcm.core.brawlstars.entity.player.Player
import com.y9vad9.bcm.core.brawlstars.entity.player.value.PlayerTag
import com.y9vad9.bcm.core.brawlstars.repository.BrawlStarsRepository
import com.y9vad9.bcm.data.brawlstars.BrawlStarsClient
import com.y9vad9.bcm.foundation.validation.annotations.ValidationDelicateApi
import io.github.reactivecircus.cache4k.Cache
import kotlin.time.Duration.Companion.minutes

class BrawlStarsRepositoryImpl(
    private val bsClient: BrawlStarsClient,
) : BrawlStarsRepository {
    private val playersCache = Cache.Builder<PlayerTag, Player>()
        .maximumCacheSize(500)
        .expireAfterWrite(1.minutes)
        .build()
    private val clubsCache = Cache.Builder<ClubTag, Club>()
        .maximumCacheSize(500)
        .expireAfterWrite(1.minutes)
        .build()

    override suspend fun getPlayer(tag: PlayerTag, withInvalidate: Boolean): Result<Player?> = runCatching {
        if (withInvalidate)
            playersCache.invalidate(tag)
        return@runCatching playersCache.get(tag) ?: bsClient.getPlayer(tag).getOrThrow()
            ?.also { player -> playersCache.put(tag, player) }
    }

    override suspend fun getClub(tag: ClubTag, withInvalidate: Boolean): Result<Club?> = runCatching {
        if (withInvalidate)
            clubsCache.invalidate(tag)
        clubsCache.get(tag) ?: bsClient.getClub(tag).getOrThrow()
            ?.also { club -> clubsCache.put(tag, club) }
    }
}