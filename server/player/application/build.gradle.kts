plugins {
    id(conventions.feature.application)
}

dependencies {
    commonMainImplementation(projects.server.player.domain)
}