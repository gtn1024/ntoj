plugins {
    application
    id("configure-kotlin")
    id("configure-ktlint")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.jackson)
    implementation(libs.logback.classic)
}

application {
    mainClass.set("zip.ntoj.judger.ApplicationKt")
}
