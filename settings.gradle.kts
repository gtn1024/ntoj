pluginManagement {
    plugins {
        id("org.jetbrains.kotlin.jvm") version "2.0.0"
    }
}

plugins {
    id("org.danilopianini.gradle-pre-commit-git-hooks") version "2.0.4"
}

rootProject.name = "ntoj"

require(JavaVersion.current() >= JavaVersion.VERSION_17) {
    "You must use at least Java 17 to build the project, you're currently using ${System.getProperty("java.version")}"
}

include(":shared")
include(":server")
include(":judger")

rootProject.children.forEach { it.configureBuildScriptName() }

fun ProjectDescriptor.configureBuildScriptName() {
    buildFileName = "${name}.gradle.kts"
    children.forEach { it.configureBuildScriptName() }
}

gitHooks {
    commitMsg { conventionalCommits() }
    preCommit {
        tasks("ktlintCheck")
        appendScript {
            """
                cd web && pnpm run lint
            """.trimIndent()
        }
    }
    createHooks()
}
