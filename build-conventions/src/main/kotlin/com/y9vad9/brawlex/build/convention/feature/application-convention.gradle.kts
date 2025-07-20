package com.y9vad9.brawlex.build.convention.feature

import kotlinx.kover.gradle.plugin.dsl.AggregationType
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit

plugins {
    id("com.y9vad9.brawlex.build.convention.multiplatform-convention")
    id("com.y9vad9.brawlex.build.convention.detekt-convention")
    id("com.y9vad9.brawlex.build.convention.kover-convention")
    id("com.y9vad9.brawlex.build.convention.multiplatform-tests-convention")
}

kover.reports.verify.rule {
    /**
     * We want to enforce the application layer to be fully tested.
     * It doesn't have the same problem with value objects constructors as in domain; therefore,
     * we can have a greater requirement here.
     */
    minBound(
        minValue = 90,
        coverageUnits = CoverageUnit.LINE,
        aggregationForGroup = AggregationType.COVERED_PERCENTAGE,
    )
}