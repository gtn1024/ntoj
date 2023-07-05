import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

dependencies {
    add("implementation", "org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    add("implementation", "org.jetbrains.kotlin:kotlin-reflect")
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}
