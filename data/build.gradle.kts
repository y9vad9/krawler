import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id(libs.plugins.conventions.jvm.get().pluginId)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    // -- KotlinX Libraries --
    implementation(libs.kotlinx.serialization)
    implementation(libs.kotlinx.coroutines)

    // -- Ktor Client (HTTP) --
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.contentNegotiation)
    implementation(libs.ktor.client.json)

    // -- Database --
    implementation(libs.exposed.core)

    // -- Cache --
    implementation(libs.cache4k)

    // -- Project --
    implementation(projects.domain)
    implementation(projects.foundation.validation)
    implementation(projects.foundation.time)
    implementation(projects.localization)

    // -- Telegram Bot API --
    implementation(libs.tgBotApi)
}