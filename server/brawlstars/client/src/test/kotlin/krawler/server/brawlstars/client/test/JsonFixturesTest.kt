package krawler.server.brawlstars.client.test

import kotlinx.serialization.json.Json

abstract class JsonFixturesTest : FixtureTest() {
    protected val json: Json = Json {
        ignoreUnknownKeys = false
    }
}
