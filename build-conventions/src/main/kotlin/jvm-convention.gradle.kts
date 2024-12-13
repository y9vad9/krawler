import org.jetbrains.kotlin.gradle.dsl.*

plugins {
    kotlin("jvm")
}

kotlin {
    jvmToolchain(19)

    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_19)
    }

    sourceSets {
        all {
            compilerOptions {
                optIn.add("kotlin.uuid.ExperimentalUuidApi")
            }
        }
    }
}