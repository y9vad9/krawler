plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    api(libs.pluginClasspath.kotlin)
    api(libs.pluginClasspath.kover)
    api(libs.pluginClasspath.detekt)
    api(libs.pluginClasspath.shadowJar)
    api(files((libs).javaClass.superclass.protectionDomain.codeSource.location))
}