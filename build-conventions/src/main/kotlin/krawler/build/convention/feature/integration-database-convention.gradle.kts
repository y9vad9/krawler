package krawler.build.convention.feature

import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("krawler.build.convention.detekt-convention")
    id("krawler.build.convention.multiplatform-convention")
    id("krawler.build.convention.multiplatform-tests-convention")
    //id("app.cash.sqldelight")
}

val libs = the<LibrariesForLibs>()

dependencies {
    commonMainImplementation(libs.sqldelight.runtime)
}