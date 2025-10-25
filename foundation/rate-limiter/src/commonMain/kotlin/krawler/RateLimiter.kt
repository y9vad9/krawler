package krawler

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Clock
import kotlin.time.Duration

/**
 * A lightweight coroutine-friendly rate limiter implementing a **token bucket** algorithm.
 *
 * It allows up to [maxRequests] acquisitions per [refillPeriod]. If no tokens are available,
 * the caller will suspend until new tokens are released.
 *
 * This implementation is coroutine-safe and uses [Clock.System] for accurate time tracking.
 *
 * Example:
 * ```
 * val limiter = RateLimiter(maxRequests = 5, refillPeriod = 1.seconds)
 *
 * repeat(10) {
 *     limiter.acquire()
 *     println("Allowed at ${Clock.System.now()}")
 * }
 * ```
 *
 * @param maxRequests the maximum number of allowed requests per [refillPeriod].
 * @param refillPeriod how often tokens are fully refilled.
 */
public class RateLimiter(
    private val maxRequests: Int,
    private val refillPeriod: Duration,
) {
    private var availableTokens = maxRequests
    private var lastRefillTime = Clock.System.now()
    private val mutex = Mutex()

    /**
     * Attempts to acquire a token immediately.
     *
     * @return `true` if a token was successfully acquired, `false` otherwise.
     */
    public suspend fun tryAcquire(): Boolean = mutex.withLock {
        refillTokensIfNeeded()
        return if (availableTokens > 0) {
            availableTokens--
            true
        } else {
            false
        }
    }

    /**
     * Suspends until a token becomes available according to the rate limit.
     *
     * This ensures that no more than [maxRequests] acquisitions occur within each
     * [refillPeriod]. The coroutine will delay until tokens are refilled if needed.
     */
    public suspend fun acquire() {
        while (true) {
            if (tryAcquire()) return
            val delayTime = mutex.withLock {
                val elapsed = Clock.System.now() - lastRefillTime
                if (elapsed < refillPeriod) refillPeriod - elapsed else Duration.ZERO
            }
            delay(delayTime)
        }
    }

    private fun refillTokensIfNeeded() {
        val now = Clock.System.now()
        val elapsed = now - lastRefillTime
        if (elapsed >= refillPeriod) {
            availableTokens = maxRequests
            lastRefillTime = now
        }
    }
}
