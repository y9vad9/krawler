package krawler.result

import kotlin.coroutines.cancellation.CancellationException

/**
 * Executes the given [block] and wraps its result in a [Result].
 *
 * This variant behaves like [runCatching], but it explicitly rethrows [CancellationException]
 * to preserve structured concurrency semantics in coroutines. In coroutine contexts,
 * swallowing a [CancellationException] can silently break cancellation propagation
 * and lead to hanging jobs or leaked coroutines.
 *
 * The broad `Exception` catch is intentional: this function aims to capture all user-level
 * exceptions while excluding coroutine cancellations.
 *
 * ### Suppress reasons
 * - **TooGenericExceptionCaught** – catching [Exception] is required to handle any
 *   user-thrown runtime exceptions while still rethrowing [CancellationException].
 * - **InstanceOfCheckForException** – we need an explicit `is CancellationException` check
 *   to correctly rethrow it and avoid swallowed cancellations.
 *
 * @param R Returning type of the lambda that is propagated to [Result].
 * @param block the code to execute safely
 * @return a [Result] containing the successful value or the caught exception
 */
@Suppress("detekt.TooGenericExceptionCaught", "detekt.InstanceOfCheckForException")
public inline fun <R> runCatchingSuspend(block: () -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        Result.failure(e)
    }
}
