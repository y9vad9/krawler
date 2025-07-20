plugins {
    id(conventions.feature.application)
}

dependencies {
    commonMainImplementation(projects.feature.user.domain)
}