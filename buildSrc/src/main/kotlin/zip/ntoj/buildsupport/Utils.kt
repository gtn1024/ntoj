package zip.ntoj.buildsupport

fun isCI() = System.getenv("CI") != null

fun getEnvironment() = System.getenv("ENV") ?: ENV_DEVELOPMENT

val ENV_PRODUCTION: String
    get() = "prod"

val ENV_DEVELOPMENT: String
    get() = "dev"
