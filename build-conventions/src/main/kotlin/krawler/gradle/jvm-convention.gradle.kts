package krawler.gradle

import gradle.kotlin.dsl.accessors._0aeddaa48b90c20082bac6881bad4e3a.sourceSets

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

                progressiveMode = true
            }
        }
    }
}
