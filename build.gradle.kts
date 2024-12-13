import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id(libs.plugins.conventions.jvm.get().pluginId)
    alias(libs.plugins.kotlinx.serialization)
    application
}

dependencies {
    // -- Database --
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.h2.database)

    // -- CLI --
    implementation(libs.clikt.core)

    // -- Project --
    implementation(projects.localization)
    implementation(projects.integration)
    implementation(projects.core)
    implementation(projects.bot)

    // -- KotlinX Libraries --
    implementation(libs.kotlinx.serialization)
    implementation(libs.kotlinx.coroutines)
}

application {
    mainClass.set("com.y9vad9.bcm.app.MainKt")
}