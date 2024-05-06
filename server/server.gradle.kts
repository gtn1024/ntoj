plugins {
    id("configure-kotlin")
    id("configure-ktlint")
    id("configure-groovy")
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.flyway)

    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
}

dependencies {
    implementation(project(":shared"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-core")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.flywaydb:flyway-database-postgresql:10.0.0")
    implementation(libs.saToken.spring)
    implementation(libs.saToken.jwt)
    implementation(libs.commons.io)
    testImplementation(libs.bundles.spock)
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

flyway {
    url = "jdbc:postgresql://${System.getenv("PG_HOST") ?: "localhost"}:${System.getenv("PG_PORT") ?: "15432"}/${
        System.getenv("PG_DATABASE") ?: "ntoj"
    }"
    user = System.getenv("PG_USER") ?: "ntoj"
    password = System.getenv("PG_PASSWORD") ?: "123456"
    schemas = arrayOf("public")
}
