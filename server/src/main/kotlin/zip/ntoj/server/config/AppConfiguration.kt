package zip.ntoj.server.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import zip.ntoj.server.service.FileService
import zip.ntoj.server.service.FileUploadService

@Configuration
@EnableConfigurationProperties(zip.ntoj.server.config.InformationConfig::class, zip.ntoj.server.config.FileConfig::class)
class AppConfiguration(
    val fileConfig: zip.ntoj.server.config.FileConfig,
    val fileUploadService: FileUploadService,
) {
    @Bean
    fun fileService(): FileService {
        return fileConfig.serviceImpl.getDeclaredConstructor(zip.ntoj.server.config.FileConfig::class.java, FileUploadService::class.java)
            .newInstance(fileConfig, fileUploadService) as FileService
    }
}
