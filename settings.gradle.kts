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
        google()
        mavenCentral()
    }
}

rootProject.name = "krawler"

includeBuild("build-conventions")

include(
    ":server:core:domain",
    ":server:bootstrap",
    ":server:feature:auth:domain",
    ":server:feature:user:domain",
    "server:feature:user:application",
)

include(
    ":telegram:bot",
)
