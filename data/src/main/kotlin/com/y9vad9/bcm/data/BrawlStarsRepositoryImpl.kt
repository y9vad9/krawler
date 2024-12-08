@file:OptIn(ValidationDelicateApi::class)

package com.y9vad9.bcm.data

import com.y9vad9.bcm.data.brawlstars.BrawlStarsClient
import com.y9vad9.bcm.data.brawlstars.entity.toBrawlStarsClub
import com.y9vad9.bcm.data.brawlstars.entity.toBrawlStarsPlayer
import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsClub
import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsPlayer
import com.y9vad9.bcm.domain.entity.brawlstars.value.ClubTag
import com.y9vad9.bcm.domain.entity.brawlstars.value.PlayerTag
import com.y9vad9.bcm.domain.repository.BrawlStarsRepository
import com.y9vad9.bcm.foundation.validation.annotations.ValidationDelicateApi
import com.y9vad9.bcm.foundation.validation.createUnsafe
import io.github.reactivecircus.cache4k.Cache
import kotlin.time.Duration.Companion.minutes

class BrawlStarsRepositoryImpl(
    private val bsClient: BrawlStarsClient,
) : BrawlStarsRepository {
    private val playersCache = Cache.Builder<PlayerTag, BrawlStarsPlayer>()
        .maximumCacheSize(500)
        .expireAfterWrite(1.minutes)
        .build()
    private val clubsCache = Cache.Builder<ClubTag, BrawlStarsClub>()
        .maximumCacheSize(500)
        .expireAfterWrite(1.minutes)
        .build()

    override suspend fun getPlayer(tag: PlayerTag, withInvalidate: Boolean): Result<BrawlStarsPlayer?> = runCatching {
        if(withInvalidate)
            playersCache.invalidate(tag)
        return@runCatching playersCache.get(tag) ?: bsClient.getPlayer(tag.toString()).getOrThrow().let {
            it?.toBrawlStarsPlayer(getClub(ClubTag.createUnsafe(it.club.tag)).getOrThrow())
        }?.also { player -> playersCache.put(tag, player) }
    }

    override suspend fun getClub(tag: ClubTag, withInvalidate: Boolean): Result<BrawlStarsClub?> = runCatching {
        if(withInvalidate)
            clubsCache.invalidate(tag)
        clubsCache.get(tag) ?: bsClient.getClub(tag.toString()).getOrThrow()?.toBrawlStarsClub()
            ?.also { club -> clubsCache.put(tag, club) }
    }
}