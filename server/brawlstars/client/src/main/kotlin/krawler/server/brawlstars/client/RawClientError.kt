package krawler.server.brawlstars.client

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Represents an error response returned by the Brawl Stars API.
 *
 * The Brawl Stars API returns this structure when a request fails due to
 * invalid input, missing resources, rate limits, or other client-side issues.
 *
 * @property reason A short machine-readable error code describing the failure reason
 *                  (e.g., `"notFound"`, `"accessDenied"`).
 * @property message An optional human-readable explanation of the error, intended
 *                   to give more context to the `reason`.
 * @property type An optional identifier describing the type of error or error category.
 * @property details An optional JSON object with additional structured data related
 *                   to the error (such as field-level validation messages).
 */
@Serializable
data class RawClientError(
    val reason: String,
    val message: String? = null,
    val type: String? = null,
    val details: JsonObject? = null,
)
