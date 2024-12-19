plugins {
    id(libs.plugins.conventions.jvm.get().pluginId)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    // -- KotlinX Libraries --
    implementation(libs.kotlinx.serialization)
    implementation(libs.kotlinx.coroutines)

    // -- Ktor Client (HTTP) --
    api(libs.ktor.client.core)
    api(libs.ktor.client.cio)
    api(libs.ktor.client.logging)
    implementation(libs.ktor.client.contentNegotiation)
    implementation(libs.ktor.client.json)

    // -- Database --
    implementation(libs.exposed.core)

    // -- Cache --
    implementation(libs.cache4k)

    // -- Project --
    implementation(projects.core)
    implementation(projects.foundation.validation)
    implementation(projects.foundation.time)
    implementation(projects.localization)
    implementation(projects.bot)

    // -- Telegram Bot API --
    implementation(libs.tgBotApi)

    // -- Logging --
    api(libs.slf4j.simple)

    // -- Test Only --
    testImplementation(libs.kotlin.test)
    testImplementation(libs.exposed.jdbc)
    testImplementation(libs.h2.database)
    testImplementation(libs.kotlinx.coroutines.test)
}