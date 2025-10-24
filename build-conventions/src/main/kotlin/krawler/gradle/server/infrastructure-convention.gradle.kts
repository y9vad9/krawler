package krawler.gradle.server

import gradle.kotlin.dsl.accessors._1c5bd38ceec3d26acee3565447f50488.main
import gradle.kotlin.dsl.accessors._1c5bd38ceec3d26acee3565447f50488.sourceSets
import gradle.kotlin.dsl.accessors._1c5bd38ceec3d26acee3565447f50488.testing
import kotlinx.kover.gradle.plugin.dsl.AggregationType
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("krawler.gradle.jvm-convention")
    id("krawler.gradle.detekt-convention")
    id("krawler.gradle.kover-convention")
    id("krawler.gradle.jvm-tests-convention")
}

val libs = the<LibrariesForLibs>()

kover {
    reports {
        verify.rule {
            minBound(
                minValue = 70,
                coverageUnits = CoverageUnit.LINE,
                aggregationForGroup = AggregationType.COVERED_PERCENTAGE,
            )
        }
    }
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

                        val envFile = rootProject.file(".env.local")
                        if (envFile.exists()) {
                            envFile.readLines()
                                .map { it.trim() }
                                .filter { it.isNotEmpty() && !it.startsWith("#") }
                                .forEach { line ->
                                    val (key, value) = line.split("=", limit = 2)
                                    environment(key, value)
                                }
                        }
                    }
                }
            }
        }
    }
}
