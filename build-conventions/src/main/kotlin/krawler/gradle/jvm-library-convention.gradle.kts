package krawler.gradle

plugins {
    id("krawler.gradle.jvm-convention")
    id("krawler.gradle.kover-convention")
    id("krawler.gradle.detekt-convention")
}

kotlin {
    explicitApi()
}
