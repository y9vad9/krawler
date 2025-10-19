package krawler.environment

/**
 * Provides access to environment variables.
 *
 * Only defines the core operator to retrieve a variable by its key.
 */
public interface Environment {

    /** Returns the value of the environment variable [key], or `null` if it is not set. */
    public operator fun get(key: String): String?
}

/**
 * Default [Environment] implementation that reads from system environment variables.
 */
public object SystemEnvironment : Environment {

    override fun get(key: String): String? = System.getenv(key)
}

/**
 * An [Environment] implementation backed by an in-memory map.
 *
 * Useful for tests or situations where environment variables need to be faked or overridden.
 *
 * @param variables initial map of environment variables
 */
public class InMemoryEnvironment(private val variables: Map<String, String> = emptyMap()) : Environment {

    override fun get(key: String): String? = variables[key]

    /**
     * Returns a new [InMemoryEnvironment] with an updated or added variable.
     */
    public fun with(key: String, value: String): InMemoryEnvironment =
        InMemoryEnvironment(variables + (key to value))
}

/**
 * Returns the value of the environment variable [key], or `null` if it is not set.
 */
public fun Environment.getOrNull(key: String): String? = this[key]

/**
 * Returns the value of the environment variable [key], or the result of [otherwise] if it is not set.
 */
public inline fun Environment.getOrElse(key: String, otherwise: () -> String): String =
    getOrNull(key) ?: otherwise()

/**
 * Returns the value of the environment variable [key], or throws an [IllegalStateException] if it is not set.
 */
public fun Environment.getOrThrow(key: String): String =
    getOrElse(key) { error("Environmental variable '$key' is not provided.") }
