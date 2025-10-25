package krawler.server.brawlstars.client.test

import java.io.InputStream

abstract class FixtureTest {
    /**
     * Loads a test fixture file from the `fixtures/` directory in the test resources as an [InputStream].
     *
     * The provided [path] is relative to the `fixtures/` directory. For example:
     * ```
     * val stream = loadFixtureAsStream("api/v1/players/battlelog/example_response_1.json")
     * ```
     * will load the file located at:
     * ```
     * src/commonTest/resources/fixtures/api/v1/players/battlelog/example_response_1.json
     * ```
     *
     * @param path The relative path to the fixture file, starting after `fixtures/`.
     * @return An [InputStream] for the fixture file.
     * @throws IllegalArgumentException if the fixture file cannot be found.
     */
    protected fun loadFixtureAsStream(path: String): InputStream {
        val resourcePath = "/fixtures/$path"
        return this::class.java.getResourceAsStream(resourcePath)
            ?: throw IllegalArgumentException("Fixture not found: $resourcePath")
    }
}
