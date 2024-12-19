package com.y9vad9.starix.data.brawlstars.pagination

import com.y9vad9.starix.core.common.entity.value.Count
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.Throws

/**
 * Interface representing a page iterator for paginated data retrieval.
 *
 * @param T The type of elements in the page.
 */
interface PagesIterator<T> {
    /**
     * Returns `true` if there is another page available, `false` otherwise.
     *
     * **Note**: Even if iterator returns true, it doesn't necessary means that
     * there should be new elements. It effectively means that we should query next page (using next)
     * and check whether there are new elements.
     *
     * @return `true` if there is another page, `false` otherwise.
     */
    operator fun hasNext(): Boolean


    /**
     * Returns `true` if there is previous page available in the context.
     */
    fun hasPrevious(): Boolean

    /**
     * Returns the next page of elements.
     *
     * @return The list of elements in the next page.
     * @throws NoSuchElementException if there are no more pages available.
     */
    @Throws(NoSuchElementException::class)
    suspend operator fun next(): Result<List<T>>

    /**
     * Returns the previous page of elements.
     *
     * @return The list of elements in the next page.
     * @throws NoSuchElementException if there are no more pages available.
     */
    @Throws(NoSuchElementException::class)
    suspend fun previous(): Result<List<T>>

    /**
     * Returns an iterator over the pages.
     *
     * @return The iterator itself.
     */
    operator fun iterator(): PagesIterator<T> = this
}

/**
 * Converts the [PagesIterator] into a [Flow] of lists of elements.
 *
 * @return A [Flow] that emits lists of elements from the page iterator.
 */
fun <T> PagesIterator<T>.asFlow(): Flow<List<T>> = flow {
    for (elements in this@asFlow)
        emit(elements.getOrThrow())
}

/**
 * Performs the given [block] on each page of elements from the [PagesIterator].
 *
 * @param block The block to be executed on each page of elements.
 */
suspend inline fun <T> PagesIterator<T>.forEachPage(block: (Result<List<T>>) -> Unit) {
    while (hasNext()) {
        block(next())
    }
}

/**
 * Applies a transformation to the elements of the current [PagesIterator].
 *
 * @param mapper The mapping function that takes an element of type [T] and returns a new element of type [R].
 * @return A new [PagesIterator] that represents the result of applying the given [mapper] function to each element of the original iterator.
 */
fun <T, R> PagesIterator<T>.map(mapper: suspend (T) -> R): PagesIterator<R> {
    return MappingPagesIterator(this, mapper)
}

fun <T> PagesIterator(
    limit: Count,
    provider: suspend (limit: Count, cursors: Cursors?) -> Result<Page<T>>,
): PagesIterator<T> = PagesIteratorImpl(provider = provider, limit = limit)

private class PagesIteratorImpl<T>(
    private val provider: suspend (limit: Count, cursors: Cursors?) -> Result<Page<T>>,
    private val limit: Count,
    private var lastCursors: Cursors? = null,
) : PagesIterator<T> {
    /**
     * Enum representing the state of the page iterator.
     *
     * - **DONE** – no more elements present.
     * - **UNKNOWN** – initial state of the iterator, needs to be queried first.
     * - **READY** – has more elements to consume (it's sdk-side decision, so there's possibility that
     * next page has no elements at all).
     */
    private enum class State {
        UNKNOWN, READY, DONE,
    }

    private var state: State = State.UNKNOWN

    override fun hasNext(): Boolean {
        return state != State.DONE
    }

    override fun hasPrevious(): Boolean {
        return state != State.UNKNOWN && lastCursors?.before != null
    }

    override suspend fun next(): Result<List<T>> {
        return provider(limit, Cursors(after = lastCursors?.after, before = null)).map {
            lastCursors = it.paging.also { if (it?.after == null) state = State.DONE }
            it.items ?: emptyList<T>()
        }
    }

    override suspend fun previous(): Result<List<T>> {
        return provider(limit, Cursors(before = lastCursors?.before, after = null)).map {
            lastCursors = it.paging.also { if (it?.after == null) state = State.DONE }
            it.items ?: emptyList<T>()
        }
    }
}

@PublishedApi
internal class MappingPagesIterator<T, R>(
    private val source: PagesIterator<T>,
    private val mapper: suspend (T) -> R,
) : PagesIterator<R> {
    override fun hasNext(): Boolean = source.hasNext()
    override fun hasPrevious(): Boolean = source.hasPrevious()

    override suspend fun next(): Result<List<R>> {
        return source.next().map {
            it.map { item -> mapper(item) }
        }
    }

    override suspend fun previous(): Result<List<R>> = source.previous().map { list ->
        list.map { mapper(it) }
    }
}