package com.y9vad9.bcm.data.cache

import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsClub
import io.github.reactivecircus.cache4k.Cache
import kotlin.time.Duration.Companion.minutes

class BrawlStarsClubCache {
    private val cache = Cache.Builder<String, BrawlStarsClub>()
        .maximumCacheSize(500)
        .expireAfterWrite(1.minutes)
        .build()

    fun add(club: BrawlStarsClub) {
        cache.put(club.tag.toString(), club)
    }

    fun get(tag: String): BrawlStarsClub? = cache.get(tag)
    suspend fun get(tag: String, loader: suspend () -> BrawlStarsClub): BrawlStarsClub? = cache.get(tag, loader)
}