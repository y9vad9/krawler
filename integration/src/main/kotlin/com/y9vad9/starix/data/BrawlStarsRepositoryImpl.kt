@file:OptIn(ValidationDelicateApi::class)

package com.y9vad9.starix.data

import com.y9vad9.starix.foundation.time.UnixTime
import com.y9vad9.starix.core.brawlstars.entity.club.Club
import com.y9vad9.starix.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.starix.core.brawlstars.entity.player.Player
import com.y9vad9.starix.core.brawlstars.entity.player.value.PlayerTag
import com.y9vad9.starix.core.brawlstars.repository.BrawlStarsRepository
import com.y9vad9.starix.data.brawlstars.BrawlStarsClient
import com.y9vad9.starix.data.database.ExcusedPlayersTable
import com.y9vad9.starix.foundation.validation.annotations.ValidationDelicateApi
import io.github.reactivecircus.cache4k.Cache
import kotlin.time.Duration.Companion.minutes

class BrawlStarsRepositoryImpl(
    private val bsClient: BrawlStarsClient,
    private val excusedPlayersTable: ExcusedPlayersTable,
) : BrawlStarsRepository {
    private val playersCache = Cache.Builder<PlayerTag, com.y9vad9.starix.core.brawlstars.entity.player.Player>()
        .maximumCacheSize(500)
        .expireAfterWrite(1.minutes)
        .build()
    private val clubsCache = Cache.Builder<ClubTag, _root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club>()
        .maximumCacheSize(500)
        .expireAfterWrite(1.minutes)
        .build()

    override suspend fun getPlayer(tag: PlayerTag, withInvalidate: Boolean): Result<com.y9vad9.starix.core.brawlstars.entity.player.Player?> = runCatching {
        if (withInvalidate)
            playersCache.invalidate(tag)
        return@runCatching playersCache.get(tag) ?: bsClient.getPlayer(tag).getOrThrow()
            ?.also { player -> playersCache.put(tag, player) }
    }

    override suspend fun getClub(tag: ClubTag, withInvalidate: Boolean): Result<_root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club?> = runCatching {
        if (withInvalidate)
            clubsCache.invalidate(tag)
        clubsCache.get(tag) ?: bsClient.getClub(tag).getOrThrow()
            ?.also { club -> clubsCache.put(tag, club) }
    }

    override suspend fun excuse(
        playerTag: PlayerTag,
        untilTime: UnixTime,
        currentTime: UnixTime,
    ): Result<Unit> = runCatching {
        excusedPlayersTable.create(
            playerTag = playerTag.toString(),
            untilTime = untilTime.inMilliseconds,
            creationTime = currentTime.inMilliseconds,
        )
    }

    override suspend fun isExcused(
        playerTag: PlayerTag,
        currentTime: UnixTime,
    ): Result<Boolean> = runCatching {
        excusedPlayersTable.exists(
            playerTag = playerTag.toString(),
            time = currentTime.inMilliseconds,
        )
    }
}