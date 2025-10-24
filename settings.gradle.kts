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
    ":foundation:logger",
)

include(
    ":server",
    ":server:auth:domain",
    ":server:auth:application",
    ":server:player:domain",
    ":server:player:application",
    ":server:brawlstars:client",
    ":server:brawlstars:database",
)

include(
    ":client:auth:domain",
    ":client:auth:application",
    ":client:player:domain",
    ":client:player:application",
)

include(
    ":telegram:bot",
)
