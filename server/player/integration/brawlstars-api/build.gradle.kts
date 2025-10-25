plugins {
    id(conventions.server.infrastructure)
}

dependencies {
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.contentNegotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    implementation(libs.kotlinx.serialization.json)

    implementation(projects.foundation.rateLimiter)
    implementation(projects.foundation.resultExt)

    testImplementation(libs.mockk)
    testImplementation(libs.ktor.client.mock)

    integrationTestImplementation(libs.ktor.client.cio)
}
