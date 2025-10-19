@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
    }
}

rootProject.name = "krawler"

includeBuild("build-conventions")

include(
    ":foundation:jvm-environment",
    ":foundation:cli-args",
)

include(
    ":server",
    ":server:core:domain",
    ":server:feature:auth:domain",
    ":server:feature:user:domain",
    "server:feature:user:application",
)

include(
    ":telegram:bot",
)
