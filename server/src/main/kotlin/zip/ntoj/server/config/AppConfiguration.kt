package zip.ntoj.server.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import zip.ntoj.server.service.FileService
import zip.ntoj.server.service.FileUploadService

@Configuration
@EnableConfigurationProperties(InformationConfig::class, FileConfig::class)
class AppConfiguration(
    val fileConfig: FileConfig,
    val fileUploadService: FileUploadService,
) {
    @Bean
    fun fileService(): FileService {
        return fileConfig.serviceImpl.getDeclaredConstructor(FileConfig::class.java, FileUploadService::class.java)
            .newInstance(fileConfig, fileUploadService) as FileService
    }
}
