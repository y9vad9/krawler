package krawler.logger

/**
 * Provides a structured and contextual logging API.
 *
 * This interface allows logging messages at various levels and supports
 * immutable context propagation via [withFields].
 *
 * Implementations may write logs to console, files, or external logging systems.
 */
public interface Logger {

    /** Logs a debug-level message. */
    public fun debug(message: String)

    /** Logs an info-level message. */
    public fun info(message: String)

    /** Logs a warning-level message. */
    public fun warning(message: String)

    /** Logs an error-level message. */
    public fun error(message: String)

    /**
     * Returns a new [Logger] instance enriched with additional context field.
     *
     * Field is merged immutably and propagated to all subsequent log calls
     * made through the returned logger.
     *
     * @param map A map of key-value pairs to add to the logger's context.
     */
    public fun withField(key: String, value: Any): Logger

    /**
     * Returns a new [Logger] instance enriched with additional context fields.
     *
     * Fields are merged immutably and propagated to all subsequent log calls
     * made through the returned logger.
     *
     * @param map A map of key-value pairs to add to the logger's context.
     */
    public fun withFields(map: Map<String, Any>): Logger
}

/**
 * Builds a new [Logger] with additional context fields using a builder DSL.
 *
 * Example:
 * ```
 * val loggerWithContext = logger.withFields {
 *     put("requestId", "abc123")
 *     put("userId", 42)
 * }
 * ```
 *
 * @param builder A DSL to populate the context fields.
 * @return A new [Logger] instance with the added fields.
 */
public inline fun Logger.withFields(builder: MutableMap<String, Any>.() -> Unit): Logger {
    return withFields(buildMap(builder))
}

/**
 * Builds a new [Logger] with additional context fields using a builder DSL.
 *
 * Example:
 * ```
 * val loggerWithContext = logger.withFields("something" to 1)
 * ```
 *
 * @param pairs List of key-value pairs.
 * @return A new [Logger] instance with the added fields.
 */
@Suppress("NOTHING_TO_INLINE") // we inline, because `mapOf` might be optimized in such case, avoiding pair creations
public inline fun Logger.withFields(vararg pairs: Pair<String, Any>): Logger {
    return withFields(mapOf(*pairs))
}

public fun Logger.withThrowable(throwable: Throwable): Logger = withField("throwable", throwable.stackTraceToString())
