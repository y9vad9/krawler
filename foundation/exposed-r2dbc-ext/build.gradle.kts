plugins {
    id(conventions.jvm.library)
}

dependencies {
    implementation(libs.exposed.core)
    implementation(libs.exposed.r2dbc)
}
