package krawler.gradle

import kotlinx.kover.gradle.plugin.dsl.AggregationType
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("krawler.gradle.multiplatform-convention")
    id("krawler.gradle.detekt-convention")
    id("krawler.gradle.kover-convention")
    id("krawler.gradle.multiplatform-tests-convention")
}

val libs = the<LibrariesForLibs>()

dependencies {
    "jvmTestImplementation"(libs.mockk)
}

kover.reports.verify.rule {
    minBound(
        minValue = 70,
        coverageUnits = CoverageUnit.LINE,
        aggregationForGroup = AggregationType.COVERED_PERCENTAGE,
    )
}
