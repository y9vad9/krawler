package com.y9vad9.brawlex.build.convention

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    jvmToolchain(11)

    sourceSets {
        all {
            compilerOptions {
                optIn.add("kotlin.uuid.ExperimentalUuidApi")
                optIn.add("kotlin.time.ExperimentalTime")

                progressiveMode = true
                freeCompilerArgs.add("-Xreturn-value-checker=full")
            }
        }
    }
}