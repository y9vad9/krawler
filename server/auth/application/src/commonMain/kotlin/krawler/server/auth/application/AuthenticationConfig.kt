package krawler.server.auth.application

import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

/**
 * Configuration parameters controlling authentication behavior and limits.
 *
 * @property activeOrFailedSessionsThreshold Maximum number of active or failed authentication sessions
 * allowed within [activeOrFailedSessionsPeriod]. Exceeding this prevents new sessions.
 * @property activeOrFailedSessionsPeriod Time window used to count recent active or failed sessions.
 * @property sessionDuration Lifetime of a single authentication session before it expires.
 * @property attemptsPerSession Maximum number of allowed authentication attempts within a session.
 * @property accessTokenDuration Lifetime of an issued access token.
 * @property refreshTokenDuration Lifetime of an issued refresh token.
 */
class AuthenticationConfig(
    val activeOrFailedSessionsThreshold: Int = 3,
    val activeOrFailedSessionsPeriod: Duration = 10.minutes,
    val sessionDuration: Duration = 10.minutes,
    val attemptsPerSession: Int = 3,
    val accessTokenDuration: Duration = 10.minutes,
    val refreshTokenDuration: Duration = 60.days,
)
