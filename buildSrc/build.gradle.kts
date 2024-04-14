plugins {
    `kotlin-dsl`
    `java-library`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(gradleApi())
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.23")
    implementation("org.jlleitschuh.gradle:ktlint-gradle:12.1.0")
}

