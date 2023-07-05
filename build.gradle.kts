plugins {
    kotlin("jvm") apply false
    java
    idea
}

allprojects {
    group = "zip.ntoj"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}

idea {
    module {
        excludeDirs = setOf(file("data"), file("build"), file("web/dist"))
    }
}
