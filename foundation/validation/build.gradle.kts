import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    id(libs.plugins.conventions.jvm.get().pluginId)
}

kotlin {
    explicitApi = ExplicitApiMode.Strict
}