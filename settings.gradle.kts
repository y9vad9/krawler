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
    ":core:domain",
)

include(
    ":feature:auth:domain",
)

include(
    ":feature:user:domain",
    ":feature:user:application",
)
