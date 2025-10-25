plugins {
    id(conventions.server.infrastructure)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    implementation(libs.exposed.core)
    implementation(libs.exposed.json)
    implementation(libs.exposed.time)
    implementation(libs.exposed.r2dbc)

    implementation(projects.foundation.exposedR2dbcExt)

    testImplementation(libs.mockk)
    testImplementation(libs.kotlin.test)

    integrationTestImplementation(libs.kotlin.test.junit5)
    integrationTestImplementation(libs.r2dbc.postgresql)
    integrationTestImplementation(libs.r2dbc.spi)
    integrationTestImplementation(libs.testcontainers.core)
    integrationTestImplementation(libs.testcontainers.postgresql)
}
