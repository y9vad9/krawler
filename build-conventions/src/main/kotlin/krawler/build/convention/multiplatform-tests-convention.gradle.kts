package krawler.build.convention

import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("krawler.build.convention.multiplatform-convention")
}

val libs = the<LibrariesForLibs>()

dependencies {
    commonTestImplementation(libs.kotlinx.coroutines.test)
    commonTestImplementation(libs.kotlin.test.junit5)
}

tasks.withType<Test> {
    useJUnitPlatform()
}