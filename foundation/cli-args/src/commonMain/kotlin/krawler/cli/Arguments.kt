package krawler.cli

@JvmInline
public value class Arguments(private val array: Array<String>) {
    /**
     * @return [Boolean] whether the given [name] was presented in array of arguments.
     */
    public fun isPresent(name: String): Boolean {
        return array.any { it.startsWith("--$name") }
    }

    /**
     * Returns value of the given argument with [name] or null.
     */
    public fun getNamedOrNull(name: String): String? {
        val index = array.indexOfFirst { it.startsWith("--$name") }
            .takeIf { it >= 0 }
            ?: return null

        return array[index]
            .substringAfter("=")
    }

    public fun getNamedList(name: String): List<String> {
        return array.withIndex()
            .filter { (_, value) -> value.startsWith("--$value") }
            .map { (index, _) -> array[index + 1] }
            .toList()
    }
}

public fun Arguments.getNamedIntOrNull(name: String): Int? =
    getNamedOrNull(name)?.toIntOrNull()

public fun Array<String>.parseArguments(): Arguments = Arguments(this)
