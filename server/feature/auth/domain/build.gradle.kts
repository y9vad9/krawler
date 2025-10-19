plugins {
    id(conventions.feature.domain)
}

dependencies {
    commonMainApi(projects.server.core.domain)
}
