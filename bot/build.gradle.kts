plugins {
    id(libs.plugins.conventions.jvm.get().pluginId)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    // -- Telegram Bots API --
    api(libs.tgBotApi)

    // -- KotlinX Libs --
    implementation(libs.kotlinx.serialization)
    implementation(libs.kotlinx.coroutines)

    // -- Project --
    implementation(projects.core)
    implementation(projects.localization)
}