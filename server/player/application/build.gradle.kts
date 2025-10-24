plugins {
    id(conventions.application)
}

dependencies {
    commonMainImplementation(projects.server.player.domain)
}