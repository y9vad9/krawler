plugins {
    id(conventions.application)
}

dependencies {
    // -- Project --
    commonMainImplementation(projects.server.auth.domain)
    commonMainImplementation(projects.foundation.logger)
}
