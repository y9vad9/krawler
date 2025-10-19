plugins {
    id(conventions.jvm.bootstrap)
}

dependencies {
    // -- Telegram Bot API --
    implementation(libs.inmo.tgbotapi)

    // -- Project Utils --
    implementation(projects.foundation.jvmEnvironment)
    implementation(projects.foundation.cliArgs)
}

application {
    mainClass.set("krawler.telegram.bot")
}

tasks.shadowJar {
    archiveBaseName.set("telegram-bot")
}
