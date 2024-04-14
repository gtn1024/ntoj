plugins {
    application
    id("configure-kotlin")
    id("configure-ktlint")
    alias(libs.plugins.shadow)
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
    mainClass.set("com.github.ntoj.app.judger.ApplicationKt")
}
