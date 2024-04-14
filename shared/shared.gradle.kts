plugins {
    id("configure-kotlin")
    id("configure-ktlint")
}

dependencies {
    api(libs.commons.codec)
    api(libs.bundles.jackson)
}
