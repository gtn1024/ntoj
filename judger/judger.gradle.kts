import zip.ntoj.buildsupport.lib

plugins {
    application
    id("configure-kotlin")
    id("configure-ktlint")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
    implementation(project(":shared"))
    implementation(lib("ktor-client-core"))
    implementation(lib("ktor-client-cio"))
    implementation(lib("ktor-client-content-negotiation"))
    implementation(lib("ktor-serialization-jackson"))
    implementation(lib("logback-classic"))
}

application {
    mainClass.set("zip.ntoj.judger.ApplicationKt")
}
