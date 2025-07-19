import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("multiplatform-convention")
}

val libs = the<LibrariesForLibs>()

dependencies {
    commonTestImplementation(libs.kotlinx.coroutines.test)
    commonTestImplementation(libs.kotlin.test)
    "jvmTestImplementation"(libs.mockk)
}

tasks.withType<Test> {
    useJUnitPlatform()
}