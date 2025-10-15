plugins {
    id(conventions.feature.application)
}

dependencies {
    commonMainImplementation(projects.server.feature.user.domain)
}