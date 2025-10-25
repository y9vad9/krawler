plugins {
    id(conventions.jvm.library)
}

dependencies {
    implementation(libs.exposed.core)
    implementation(libs.exposed.json)
    implementation(libs.exposed.time)
    implementation(libs.exposed.r2dbc)
}
