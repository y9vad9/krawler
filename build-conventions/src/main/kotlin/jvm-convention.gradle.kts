import org.jetbrains.kotlin.gradle.dsl.*

plugins {
    kotlin("jvm")
}

kotlin {
    jvmToolchain(11)

    sourceSets {
        all {
            compilerOptions {
                optIn.add("kotlin.uuid.ExperimentalUuidApi")
            }
        }
    }
}