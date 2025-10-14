package com.y9vad9.krawler.build.convention

import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("com.y9vad9.krawler.build.convention.jvm-convention")
}

val libs = the<LibrariesForLibs>()

dependencies {
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlin.test.junit5)
}

tasks.withType<Test> {
    useJUnitPlatform()
}