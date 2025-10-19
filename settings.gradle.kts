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
    ":server:auth:domain",
    ":server:player:domain",
    "server:player:application",
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
