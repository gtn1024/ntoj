package zip.ntoj.buildsupport

import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType

fun isCI() = System.getenv("CI") != null

fun getEnvironment() = System.getenv("ENV") ?: ENV_DEVELOPMENT

val ENV_PRODUCTION: String
    get() = "prod"

val ENV_DEVELOPMENT: String
    get() = "dev"

fun Project.lib(id: String): Provider<MinimalExternalModuleDependency> {
    val versionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")
    return versionCatalog.findLibrary(id).get()
}

fun Project.bundle(id: String): Provider<ExternalModuleDependencyBundle> {
    val versionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")
    return versionCatalog.findBundle(id).get()
}
