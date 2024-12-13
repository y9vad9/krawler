plugins {
    id(libs.plugins.conventions.jvm.get().pluginId)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    // -- KotlinX Libs --
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.serialization)
    implementation(libs.kotlinx.datetime)

    // -- Project --
    api(projects.foundation.validation)
    api(projects.foundation.time)
}