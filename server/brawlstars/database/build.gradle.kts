plugins {
    id(conventions.server.infrastructure)
    alias(libs.plugins.cashapp.sqldelight)
}

dependencies {
    implementation(libs.exposed.core)
    implementation(libs.exposed.json)
    implementation(libs.exposed.time)
    implementation(libs.exposed.r2dbc)
}
