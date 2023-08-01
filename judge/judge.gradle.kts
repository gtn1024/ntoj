val ktor_version = "2.3.2"

plugins {
    id("configure-kotlin")
    id("configure-ktlint")
    id("io.ktor.plugin") version "2.3.2"
}

dependencies {
    implementation(project(":shared"))
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")

    // logback
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("ch.qos.logback:logback-core:1.4.8")
    implementation("ch.qos.logback:logback-classic:1.4.8")

    // jackson
    implementation("io.ktor:ktor-serialization-jackson:$ktor_version")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
}
