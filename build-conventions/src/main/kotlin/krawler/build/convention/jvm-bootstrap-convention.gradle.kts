package krawler.build.convention

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.kotlin.dsl.invoke

plugins {
    id("krawler.build.convention.jvm-convention")
    id("krawler.build.convention.jvm-tests-convention")
    id("krawler.build.convention.kover-convention")
    id("krawler.build.convention.detekt-convention")
    id("com.gradleup.shadow")
    application
    `jvm-test-suite`
}

val libs = the<LibrariesForLibs>()

dependencies {
    testImplementation(libs.kotlin.test)
}

testing {
    @Suppress("UnstableApiUsage")
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }

        val functionalTest = register<JvmTestSuite>("functionalTest") {
            dependencies {
                implementation(project())
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutines.test)
                implementation(sourceSets.main.get().compileClasspath)
            }

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                    }
                }
            }
        }

        register<JvmTestSuite>("integrationTest") {
            dependencies {
                implementation(project())
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutines.test)
                implementation(sourceSets.main.get().compileClasspath)
            }

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(functionalTest)
                    }
                }
            }
        }
    }
}

tasks.shadowJar {
    archiveClassifier.set("")
}

tasks.distZip {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.distTar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.startScripts {
    dependsOn(tasks.shadowJar)
}
