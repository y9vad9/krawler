package krawler.gradle

import kotlinx.kover.gradle.plugin.dsl.AggregationType
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("krawler.gradle.detekt-convention")
    id("krawler.gradle.multiplatform-convention")
    id("krawler.gradle.multiplatform-tests-convention")
    id("krawler.gradle.kover-convention")
}

val libs = the<LibrariesForLibs>()

kotlin {
    explicitApi()
}

kover {
    reports {
        verify.rule {
            /**
             * We want to enforce domain to be fully tested as logic there
             * is totally deterministic, pure; therefore, should be well-tested.
             *
             * Ideally it would be 100%, but as there is a problem with providing coverage
             * for a value object constructors, thus we have only 85%.
             */
            minBound(
                minValue = 85,
                coverageUnits = CoverageUnit.LINE,
                aggregationForGroup = AggregationType.COVERED_PERCENTAGE,
            )
        }
    }
}