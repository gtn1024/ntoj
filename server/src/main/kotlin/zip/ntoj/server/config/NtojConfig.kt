package zip.ntoj.server.config

import org.springframework.boot.context.properties.ConfigurationProperties
import zip.ntoj.server.service.FileSystemFileService

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
