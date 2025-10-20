plugins {
    id(conventions.feature.application)
}

dependencies {
    // -- Project --
    commonMainImplementation(projects.server.auth.domain)
    commonMainImplementation(projects.foundation.logger)
}
