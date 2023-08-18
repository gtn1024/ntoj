import zip.ntoj.buildsupport.bundle
import zip.ntoj.buildsupport.lib

plugins {
    id("configure-kotlin")
    id("configure-ktlint")
}

dependencies {
    api(lib("commons-codec"))
    implementation(bundle("jackson"))
}
