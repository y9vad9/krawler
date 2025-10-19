package krawler.build.convention

import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("krawler.build.convention.jvm-convention")
}

val libs = the<LibrariesForLibs>()

dependencies {
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlin.test.junit5)
}

tasks.withType<Test> {
    useJUnitPlatform()
}