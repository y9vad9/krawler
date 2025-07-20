package com.y9vad9.brawlex.build.convention.feature

import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("com.y9vad9.brawlex.build.convention.detekt-convention")
    id("com.y9vad9.brawlex.build.convention.multiplatform-convention")
    id("com.y9vad9.brawlex.build.convention.multiplatform-tests-convention")
    //id("app.cash.sqldelight")
}

val libs = the<LibrariesForLibs>()

dependencies {
    commonMainImplementation(libs.sqldelight.runtime)
}