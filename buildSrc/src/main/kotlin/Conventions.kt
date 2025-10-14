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
    val jvm: String = "com.y9vad9.krawler.build.convention.jvm-convention",
    val kover: String = "com.y9vad9.krawler.build.convention.kover-convention",
    val detekt: String = "com.y9vad9.krawler.build.convention.detekt-convention",
    val multiplatform: MultiplatformNamespace = MultiplatformNamespace(),
    val feature: FeatureNamespace = FeatureNamespace(),
)

class JvmNamespace internal constructor(
    val core: String = "com.y9vad9.krawler.build.convention.jvm-convention",
    val tests: String = "com.y9vad9.krawler.build.convention.jvm-tests-convention",
)


/**
 * Convention plugins used in Kotlin Multiplatform projects.
 */
class MultiplatformNamespace internal constructor(
    val core: String = "com.y9vad9.krawler.build.convention.multiplatform-convention",
    /** Convention plugin for setting up shared multiplatform libraries. */
    val library: String = "com.y9vad9.krawler.build.convention.multiplatform-library-convention",
    val tests: String = "com.y9vad9.krawler.build.convention.multiplatform-tests-convention"
)

class FeatureNamespace internal constructor(
    val domain: String = "com.y9vad9.krawler.build.convention.feature.domain-convention",
    val application: String = "com.y9vad9.krawler.build.convention.feature.application-convention",
)
