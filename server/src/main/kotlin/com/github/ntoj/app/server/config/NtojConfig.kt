package com.github.ntoj.app.server.config

import com.github.ntoj.app.server.service.FileSystemFileService
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "ntoj.information")
data class InformationConfig(
    val name: String = "NTOJ",
    val beian: String = "",
)

@ConfigurationProperties(prefix = "ntoj.file")
data class FileConfig(
    val serviceImpl: Class<*> = FileSystemFileService::class.java,
    val fileSystem: FileSystemFileConfig,
) {
    data class FileSystemFileConfig(
        val baseDir: String = "data",
    )
}
