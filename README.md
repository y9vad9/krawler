# <img src="https://upload.wikimedia.org/wikipedia/commons/thumb/3/37/Kotlin_Icon_2021.svg/2048px-Kotlin_Icon_2021.svg.png" width=24 height=24 /> Kotlin Project Template

Project template for convenient Kotlin project setup using [Gradle convention plugins](https://docs.gradle.org/current/samples/sample_convention_plugins.html#compiling_convention_plugins) and [version catalogs](https://docs.gradle.org/current/userguide/platforms.html#sub:version-catalog).

## Motivation

This template is intended to reduce the repetitive setup work required when starting new Kotlin projects by providing reusable conventions, optimized Gradle settings, and CI workflows out of the box.

## Initializing

- `settings.gradle.kts`: Set your root project name.
- `gradle/libs.versions.toml`: Define your dependencies and plugin versions.

> **Note**  
> [Type-safe project accessors](https://docs.gradle.org/current/userguide/declaring_dependencies.html#sec:type-safe-project-accessors) are enabled by default.
> To disable them, remove the corresponding feature flag from `settings.gradle.kts`.

## Built-ins

### Build Conventions

This template provides reusable and composable [build conventions](build-conventions/src/main/kotlin) to simplify Gradle configuration.

#### Available Convention Plugins

| Convention ID                      | Description                                                                           |
|------------------------------------|---------------------------------------------------------------------------------------|
| `jvm-convention`                   | Applies basic Kotlin/JVM configuration with Java toolchain set to 11.                 |
| `kover-convention`                 | Applies the Kover plugin with no additional configuration.                            |
| `detekt-convention`                | Enables Detekt with all rules active and parallel analysis enabled.                   |
| `tests-convention`                 | Adds testing dependencies: `mockk` for `jvmTest`, and `kotlin.test` for `commonMain`. |
| `multiplatform-convention`         | Configures Kotlin Multiplatform with JVM as default, Java toolchain set to 11.        |
| `multiplatform-library-convention` | Extends `multiplatform-convention` and enables the `explicitApi` mode.                |

#### Usage Example

```kotlin
plugins {
    id(conventions.jvm)
}
```

> `conventions` is a property exposed by the template via `buildSrc`, providing easy access to convention plugin identifiers.

### Gradle Properties

The template comes preconfigured with sensible defaults in `gradle.properties` to optimize performance and compatibility across development and CI environments.

#### Enabled by Default

The template configures several performance and compatibility-related options in `gradle.properties` to provide a faster development experience:

- **Gradle Daemon**  
  Enabled via `org.gradle.daemon=true` to avoid JVM startup cost on repeated builds.

- **Parallel Task Execution**  
  Enabled via `org.gradle.parallel=true` to allow independent modules to build in parallel.

- **On-Demand Configuration**  
  Enabled via `org.gradle.configureondemand=true` to avoid configuring unnecessary projects during a build.

- **Build Cache**  
  Enabled via `org.gradle.caching=true` to reuse outputs from previous builds.

- **Kotlin Incremental Compilation**  
  Enabled via:
    - `kotlin.incremental=true`
    - `kotlin.incremental.compilation=true`  
      This speeds up builds by compiling only changed sources and supporting inter-module compilation.

- **Kotlin and Gradle Daemon Usage**  
  Enabled via:
    - `kotlin.daemon=true`
    - `kotlin.compiler.execution.strategy=daemon`  
      Ensures Kotlin compilation runs via a persistent compiler daemon.

- **Configuration Cache**  
  Enabled via:
    - `org.gradle.configuration-cache=true`
    - `org.gradle.configuration-cache.problems=fail`  
      Ensures that builds are reproducible and fail early on cache-incompatible configurations.

- **JVM Memory Settings**  
  Configured via `org.gradle.jvmargs=-Xmx8G -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8`  
  Provides 8 GB max heap for Gradle daemon and enables heap dumps on out-of-memory errors.

- **Kotlin/JS Warnings Suppression**  
  Suppresses experimental JS Canvas warnings with `org.jetbrains.compose.experimental.jscanvas.enabled=true`.  
  Useful when JS targets are disabled in some environments (e.g., CI).

These properties can be modified or disabled as needed for your specific project setup or CI environment.

## CI Workflows

This template includes pre-configured GitHub Actions workflows, enabled for all pull requests and pushes to the `main` branch:

- [Code Quality (CodeQL)](.github/workflows/analyse.codeql.yml): Static analysis for potential security vulnerabilities and quality issues.
- [Coverage Check](.github/workflows/check.coverage.yml): Verifies test coverage based on configured thresholds.
- [Detekt](.github/workflows/check.detekt.yml): Runs static analysis using Detekt with full rule set.
- [Tests](.github/workflows/check.tests.yml): Executes unit tests and reports results.

### Dependabot

The repository includes [Dependabot configuration](.github/dependabot.yml) for automatic updates to dependencies, plugins, and GitHub Actions.