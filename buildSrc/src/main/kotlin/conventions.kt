@file:Suppress("unused")

/**
 * Defines access to all available convention plugin IDs used in this Gradle project.
 *
 * These constants are manually updated and help avoid typos or duplication when referencing
 * convention plugins from the included build `build-conventions`.
 *
 * Example usage:
 * ```kotlin
 * plugins {
 *     id(conventions.jvm)
 *     id(conventions.feature.di)
 *     id(conventions.feature.database)
 * }
 * ```
 */
val conventions: ConventionNamespace = ConventionNamespace()

/**
 * Top-level namespace that organizes convention plugins into meaningful groups
 * (e.g., `jvm`, `multiplatform`, etc.).
 */
class ConventionNamespace internal constructor(
    val jvm: JvmNamespace = JvmNamespace(),
    val kover: String = "krawler.gradle.kover-convention",
    val detekt: String = "krawler.gradle.detekt-convention",
    val multiplatform: MultiplatformNamespace = MultiplatformNamespace(),
    val domain: String = "krawler.gradle.domain-convention",
    val application: String = "krawler.gradle.application-convention",
    val server: ServerNamespace = ServerNamespace(),
)

class JvmNamespace internal constructor(
    val core: String = "krawler.gradle.jvm-convention",
    val tests: String = "krawler.gradle.jvm-tests-convention",
    val library: String = "krawler.gradle.jvm-library-convention",
    val bootstrap: String = "krawler.gradle.jvm-bootstrap-convention",
)


/**
 * Convention plugins used in Kotlin Multiplatform projects.
 */
class MultiplatformNamespace internal constructor(
    val core: String = "krawler.gradle.multiplatform-convention",
    /** Convention plugin for setting up shared multiplatform libraries. */
    val library: String = "krawler.gradle.multiplatform-library-convention",
    val tests: String = "krawler.gradle.multiplatform-tests-convention"
)

/**
 * Convention plugins used in Server modules.
 */
class ServerNamespace internal constructor(
    val infrastructure: String = "krawler.gradle.server.infrastructure-convention",
    val integration: String = "krawler.gradle.server.integration-convention",
)
