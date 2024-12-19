plugins {
    id(libs.plugins.conventions.jvm.get().pluginId)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.mockative)
}

dependencies {
    // -- KotlinX Libs --
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.serialization)
    implementation(libs.kotlinx.datetime)

    // -- Project --
    api(projects.foundation.validation)
    api(projects.foundation.time)

    // -- Tests --
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.mockative)
}