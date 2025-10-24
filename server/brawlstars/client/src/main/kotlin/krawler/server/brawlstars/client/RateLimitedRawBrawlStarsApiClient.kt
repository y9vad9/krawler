package krawler.server.brawlstars.client

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import krawler.server.brawlstars.client.BrawlStarsApiClient.Companion
import krawler.server.brawlstars.client.battle.RawBattleRecord
import krawler.server.brawlstars.client.club.RawClub
import krawler.server.brawlstars.client.club.RawClubMember
import krawler.server.brawlstars.client.event.RawScheduledEvent
import krawler.server.brawlstars.client.pagination.RawPaginatedList
import krawler.server.brawlstars.client.player.RawPlayer
import krawler.server.brawlstars.client.ranking.RawClubRanking
import krawler.server.brawlstars.client.ranking.RawPlayerRanking
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * Rate-Limited Brawl Stars API Client.
 *
 * Wraps another [BrawlStarsApiClient] and enforces a global rate limit across all API requests.
 *
 * @param delegate The underlying [BrawlStarsApiClient] to forward calls to.
 * @param maxRequests Maximum number of requests allowed per [per] duration.
 * @param per The time window for rate limiting.
 * @param clock Clock to be used to measure throttling.
 */
@OptIn(ExperimentalTime::class)
class RateLimitedRawBrawlStarsApiClient(
    private val delegate: BrawlStarsApiClient,
    maxRequests: Int,
    per: Duration,
    private val clock: Clock = Clock.System
) : BrawlStarsApiClient {
    private val mutex = Mutex()
    private var lastRequestTime = 0L
    private val intervalMs = (per.inWholeMilliseconds / maxRequests)

    private suspend fun <T> rateLimitedCall(block: suspend () -> T): T {
        mutex.withLock {
            val now = clock.now().toEpochMilliseconds()
            val waitTime = lastRequestTime + intervalMs - now
            if (waitTime > 0) delay(waitTime)
            lastRequestTime = Clock.System.now().toEpochMilliseconds()
        }
        return block()
    }


    /**
     * Retrieves a player by their tag.
     *
     * @param tag The unique tag of the player.
     * @return A [Result] wrapping the [RawPlayer] if found, or null if the player does not exist,
     *         or a failure if the request failed.
     */
    override suspend fun getPlayer(tag: String): Result<RawPlayer?> =
        rateLimitedCall { delegate.getPlayer(tag) }

    /**
     * Retrieves the battle log for a specific player.
     *
     * The battle log contains recent battles the player participated in.
     *
     * @param tag The unique tag of the player.
     * @return A [Result] wrapping a list of battle records.
     */
    override suspend fun getPlayerBattleLog(tag: String): Result<List<RawBattleRecord>?> =
        rateLimitedCall { delegate.getPlayerBattleLog(tag) }

    /**
     * Retrieves a club by its tag.
     *
     * @param tag The unique tag of the club.
     * @return A [Result] wrapping the [RawClub] if found, or null if the club does not exist,
     *         or a failure if the request failed.
     */
    override suspend fun getClub(tag: String): Result<RawClub?> =
        rateLimitedCall { delegate.getClub(tag) }

    /**
     * Retrieves the list of members for a club by its tag.
     *
     * @param tag The unique tag of the club.
     * @return A [Result] wrapping the list of [RawClubMember] objects, or a failure if the request failed.
     */
    override suspend fun getClubMembers(tag: String): Result<List<RawClubMember>?> =
        rateLimitedCall { delegate.getClubMembers(tag) }

    /**
     * Retrieves player rankings for a specific brawler.
     *
     * Supports pagination using the `after` and `before` cursors from a previous [RawPaginatedList].
     *
     * @param brawlerId The ID of the brawler.
     * @param countryCode The country code for rankings (default is [Companion.DEFAULT_COUNTRY_CODE]).
     * @param after Optional cursor to return results **after** the given item, for pagination.
     * @param before Optional cursor to return results **before** the given item, for pagination.
     * @param limit Optional parameter to limit the number of results returned.
     * @return A [Result] wrapping a [RawPaginatedList] of [RawPlayerRanking], or a failure if the request failed.
     */
    override suspend fun getBrawlerRanking(
        brawlerId: Int,
        countryCode: String,
        after: String?,
        before: String?,
        limit: Int?
    ): Result<RawPaginatedList<RawPlayerRanking>> =
        rateLimitedCall { delegate.getBrawlerRanking(brawlerId, countryCode, after, before, limit) }

    /**
     * Retrieves overall player rankings for a specific brawler.
     *
     * Supports pagination using the `after` and `before` cursors from a previous [RawPaginatedList].
     *
     * @param countryCode The country code for rankings (default is [Companion.DEFAULT_COUNTRY_CODE]).
     * @param after Optional cursor to return results **after** the given item, for pagination.
     * @param before Optional cursor to return results **before** the given item, for pagination.
     * @param limit Optional parameter to limit the number of results returned.
     * @return A [Result] wrapping a [RawPaginatedList] of [RawPlayerRanking], or a failure if the request failed.
     */
    override suspend fun getPlayerRanking(
        countryCode: String,
        after: String?,
        before: String?,
        limit: Int?
    ): Result<RawPaginatedList<RawPlayerRanking>> =
        rateLimitedCall { delegate.getPlayerRanking(countryCode, after, before, limit) }

    /**
     * Retrieves club rankings.
     *
     * Supports pagination using the `after` and `before` cursors from a previous [RawPaginatedList].
     *
     * @param countryCode The country code for rankings (default is [DEFAULT_COUNTRY_CODE]).
     * @param after Optional cursor to return results **after** the given item, for pagination.
     * @param before Optional cursor to return results **before** the given item, for pagination.
     * @param limit Optional parameter to limit the number of results returned.
     * @return A [Result] wrapping a [RawPaginatedList] of [RawClubRanking], or a failure if the request failed.
     */
    override suspend fun getClubRanking(
        countryCode: String,
        after: String?,
        before: String?,
        limit: Int?
    ): Result<RawPaginatedList<RawClubRanking>> =
        rateLimitedCall { delegate.getClubRanking(countryCode, after, before, limit) }

    /**
     * Retrieves the scheduled event rotation in Brawl Stars.
     *
     * @return A list of [RawScheduledEvent] representing upcoming events.
     */
    override suspend fun getEventsRotation(): Result<List<RawScheduledEvent>> =
        rateLimitedCall { delegate.getEventsRotation() }
}
