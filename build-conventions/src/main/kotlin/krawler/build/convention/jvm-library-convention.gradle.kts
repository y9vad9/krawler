package krawler.build.convention

plugins {
    id("krawler.build.convention.jvm-convention")
    id("krawler.build.convention.kover-convention")
    id("krawler.build.convention.detekt-convention")
}

kotlin {
    explicitApi()
}
