plugins {
    id(libs.plugins.conventions.jvm.get().pluginId)
}

dependencies {
    // -- KotlinX Libs --
    implementation(libs.kotlinx.coroutines)

    // -- Project --
    api(projects.foundation.validation)
    api(projects.foundation.time)
}