import kotlinx.kover.gradle.plugin.dsl.AggregationType
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit

plugins {
    id(conventions.application)
}

dependencies {
    // -- Project --
    commonMainImplementation(projects.server.auth.domain)
    commonMainImplementation(projects.foundation.logger)
}

kover.reports.verify.rule {
    minBound(
        minValue = 85,
        coverageUnits = CoverageUnit.LINE,
        aggregationForGroup = AggregationType.COVERED_PERCENTAGE,
    )
}
