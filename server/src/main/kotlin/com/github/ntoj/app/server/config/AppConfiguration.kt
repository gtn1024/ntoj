package com.github.ntoj.app.server.config

import com.github.ntoj.app.server.service.FileService
import com.github.ntoj.app.server.service.FileUploadService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

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
