package krawler.gradle.server

import kotlinx.kover.gradle.plugin.dsl.AggregationType
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("krawler.gradle.jvm-convention")
    id("krawler.gradle.detekt-convention")
    id("krawler.gradle.kover-convention")
    id("krawler.gradle.jvm-tests-convention")
    `jvm-test-suite`
}

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

val libs = the<LibrariesForLibs>()

testing {
    @Suppress("UnstableApiUsage")
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
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
                        shouldRunAfter(test)
                    }
                }
            }
        }
    }
}
