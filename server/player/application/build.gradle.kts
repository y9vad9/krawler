import kotlinx.kover.gradle.plugin.dsl.AggregationType
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit

plugins {
    id(conventions.application)
}

dependencies {
    commonMainImplementation(projects.foundation.logger)
}

kover.reports.verify.rule {
    minBound(
        minValue = 70, // we leave here less minValue than usual, because feature is total data-driven
        coverageUnits = CoverageUnit.LINE,
        aggregationForGroup = AggregationType.COVERED_PERCENTAGE,
    )
}
