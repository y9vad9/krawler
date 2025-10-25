package krawler.gradle.server

import kotlinx.kover.gradle.plugin.dsl.AggregationType
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.plugins.jvm.JvmTestSuite
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.getting
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.`jvm-test-suite`
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.the

plugins {
    id("krawler.gradle.jvm-convention")
    id("krawler.gradle.detekt-convention")
    id("krawler.gradle.kover-convention")
    id("krawler.gradle.jvm-tests-convention")
    `jvm-test-suite`
}

val libs = the<LibrariesForLibs>()

dependencies {
    implementation(libs.koin.core)
    implementation(libs.koin.annotations)
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

testing {
    @Suppress("UnstableApiUsage")
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()

            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutines.test)
            }
        }

        register<JvmTestSuite>("integrationTest") {
            dependencies {
                implementation(project())
                implementation(libs.kotlin.test)
                implementation(libs.kotlin.test.junit5)
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
