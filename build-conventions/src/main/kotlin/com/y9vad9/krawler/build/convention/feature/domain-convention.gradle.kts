package com.y9vad9.krawler.build.convention.feature

import kotlinx.kover.gradle.plugin.dsl.AggregationType
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("com.y9vad9.krawler.build.convention.detekt-convention")
    id("com.y9vad9.krawler.build.convention.multiplatform-convention")
    id("com.y9vad9.krawler.build.convention.multiplatform-tests-convention")
    id("com.y9vad9.krawler.build.convention.kover-convention")
}

val libs = the<LibrariesForLibs>()

dependencies {
    commonMainApi(libs.y9vad9.valdi.validation)
    commonMainImplementation(libs.y9vad9.valdi.domain)

    detektPlugins(libs.y9vad9.valdi.domain.detekt)
}

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
             * for a value object constructors, we have only 85%.
             */
            minBound(
                minValue = 85,
                coverageUnits = CoverageUnit.LINE,
                aggregationForGroup = AggregationType.COVERED_PERCENTAGE,
            )
        }
    }
}