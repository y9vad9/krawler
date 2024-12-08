package com.y9vad9.bcm.data.cache

import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsPlayer
import io.github.reactivecircus.cache4k.Cache
import kotlin.time.Duration.Companion.minutes

class BrawlStarsPlayerCache {
    private val cache = Cache.Builder<String, BrawlStarsPlayer>()
        .maximumCacheSize(500)
        .expireAfterWrite(1.minutes)
        .build()

    fun add(player: BrawlStarsPlayer) {
        cache.put(player.tag.toString(), player)
    }

    fun get(tag: String): BrawlStarsPlayer? = cache.get(tag)
}