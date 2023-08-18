import zip.ntoj.buildsupport.lib

plugins {
    id("configure-kotlin")
    id("configure-ktlint")
}

dependencies{
    implementation(project(":shared"))
    implementation(lib("ktor-client-core"))
    implementation(lib("ktor-client-cio"))
    implementation(lib("ktor-client-content-negotiation"))
    implementation(lib("ktor-serialization-jackson"))
}
