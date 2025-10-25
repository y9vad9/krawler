plugins {
    id(conventions.server.integration)
}

dependencies {
    implementation(projects.server.player.integration.brawlstarsApi)
    implementation(projects.server.player.integration.database)
}
