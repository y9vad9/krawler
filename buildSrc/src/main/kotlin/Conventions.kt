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
    val kover: String = "krawler.build.convention.kover-convention",
    val detekt: String = "krawler.build.convention.detekt-convention",
    val multiplatform: MultiplatformNamespace = MultiplatformNamespace(),
    val feature: FeatureNamespace = FeatureNamespace(),
)

class JvmNamespace internal constructor(
    val core: String = "krawler.build.convention.jvm-convention",
    val tests: String = "krawler.build.convention.jvm-tests-convention",
    val library: String = "krawler.build.convention.jvm-library-convention",
    val bootstrap: String = "krawler.build.convention.jvm-bootstrap-convention",
)


/**
 * Convention plugins used in Kotlin Multiplatform projects.
 */
class MultiplatformNamespace internal constructor(
    val core: String = "krawler.build.convention.multiplatform-convention",
    /** Convention plugin for setting up shared multiplatform libraries. */
    val library: String = "krawler.build.convention.multiplatform-library-convention",
    val tests: String = "krawler.build.convention.multiplatform-tests-convention"
)

class FeatureNamespace internal constructor(
    val domain: String = "krawler.build.convention.feature.domain-convention",
    val application: String = "krawler.build.convention.feature.application-convention",
)
