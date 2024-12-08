import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id(libs.plugins.conventions.jvm.get().pluginId)
}

dependencies {
    implementation(libs.kotlinx.coroutines)
    implementation(projects.foundation.validation)
    implementation(projects.foundation.time)
}