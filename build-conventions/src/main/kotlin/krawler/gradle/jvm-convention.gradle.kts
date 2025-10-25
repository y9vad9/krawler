package krawler.gradle

plugins {
    kotlin("jvm")
}

kotlin {
    jvmToolchain(11)

    sourceSets {
        all {
            compilerOptions {
                optIn.add("kotlin.uuid.ExperimentalUuidApi")
                optIn.add("kotlin.time.ExperimentalTime")
                optIn.add("kotlinx.serialization.ExperimentalSerializationApi")
            }
        }
    }
}
