plugins {
    id(conventions.jvm.bootstrap)
}

dependencies {
    // -- Coroutines --
    implementation(libs.kotlinx.coroutines)

    // -- RPC --
    implementation(libs.grpc.kotlin.stub)
    implementation(libs.grpc.netty)
    implementation(libs.protobuf.kotlin)

    // -- DI --
    implementation(libs.koin.core)

    // -- Project Utils --
    implementation(projects.foundation.jvmEnvironment)
    implementation(projects.foundation.cliArgs)

    // -- Mock --
    functionalTestImplementation(libs.mockk)
}

application {
    mainClass.set("krawler.server.MainKt")
}

tasks.shadowJar {
    archiveBaseName.set("server-app")
}
