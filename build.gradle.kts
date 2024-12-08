import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id(libs.plugins.conventions.jvm.get().pluginId)
    alias(libs.plugins.kotlinx.serialization)
    application
}

dependencies {
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.h2.database)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.contentNegotiation)
    implementation(libs.ktor.client.json)
    implementation(libs.tgBotApi)

    implementation(libs.kotlinx.serialization)
    implementation(libs.kotlinx.coroutines)
}

kotlin {
    jvmToolchain(17)

    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

application {
    mainClass.set("com.y9vad9.gamerslegion.MainKt")
}