package krawler.server.brawlstars.client

/**
 * Base sealed exception type for all errors returned by the Brawl Stars API.
 *
 * Each specific subclass corresponds to a distinct HTTP status code or
 * error category defined by the API. All exceptions contain the
 * [RawClientError] payload returned by the server, providing both
 * machine-readable and human-readable error details.
 *
 * @param message Human-readable message to the exception. It can be either
 *                a default one or the one from [ClientError.message].
 * @property error The parsed error payload returned by the Brawl Stars API.
 */
sealed class BrawlStarsApiException(
    message: String,
    val error: RawClientError,
) : Exception(message)

/**
 * Thrown when the request contains invalid parameters or fails validation
 * (HTTP 400 — `badRequest`).
 */
class BadRequestException(error: RawClientError) : BrawlStarsApiException(
    message = error.message ?: "Client provided incorrect parameters for the request.",
    error = error,
)

/**
 * Thrown when access to a resource is denied due to missing or incorrect
 * credentials, or when the API token does not grant access to the requested
 * resource (HTTP 403 — `accessDenied`).
 */
class AccessDeniedException(error: RawClientError) : BrawlStarsApiException(
    message = error.message
        ?: ("Access denied, either because of missing/incorrect credentials or used API token does not grant access" +
                " to the requested resource."),
    error = error,
)

/**
 * Thrown when the request exceeds the allowed rate limit for the used
 * API token (HTTP 429 — `tooManyRequests`).
 */
class LimitsExceededException(error: RawClientError) : BrawlStarsApiException(
    message = error.message ?: ("Request was throttled, because amount of requests was above the threshold defined" +
            " for the used API token."),
    error = error,
)

/**
 * Thrown when the server encounters an unexpected error while processing
 * the request (HTTP 500 — `internalError`).
 */
class InternalServerErrorException(error: RawClientError) : BrawlStarsApiException(
    message = error.message ?: "Unknown error happened when handling the request.",
    error = error,
)

/**
 * Thrown when the service is temporarily unavailable due to maintenance
 * (HTTP 503 — `serviceUnavailable`).
 */
class UnderMaintenanceException(error: RawClientError) : BrawlStarsApiException(
    message = error.message ?: "Service is temporarily unavailable because of maintenance.",
    error = error,
)
