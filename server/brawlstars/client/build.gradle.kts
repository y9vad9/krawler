plugins {
    id(conventions.server.infrastructure)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    // -- Http Client --
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.contentNegotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    // -- Serialization --
    implementation(libs.kotlinx.serialization.json)

    // -- Unit Tests --
    testImplementation(libs.kotlin.test)
    testImplementation(libs.mockk)
    testImplementation(libs.ktor.client.mock)

    // -- Integration Tests --
    integrationTestImplementation(libs.kotlin.test.junit5)
    integrationTestImplementation(libs.ktor.client.cio)
}
