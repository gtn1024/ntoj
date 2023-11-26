package zip.ntoj.server.config

import cn.dev33.satoken.interceptor.SaInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import zip.ntoj.server.config.interceptor.JudgeClientInterceptor
import zip.ntoj.server.service.JudgeClientTokenService
import zip.ntoj.server.service.JudgerSystemStatusService

@Configuration
class WebMvcConfig(
    val fileConfig: FileConfig,
    val judgeClientTokenService: JudgeClientTokenService,
    val judgerSystemStatusService: JudgerSystemStatusService,
) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(JudgeClientInterceptor(judgeClientTokenService, judgerSystemStatusService))
            .addPathPatterns("/judge_client/**")
        registry.addInterceptor(SaInterceptor()).addPathPatterns("/**")
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("upload/**")
            .addResourceLocations("file:" + fileConfig.fileSystem.baseDir + "/upload/")
    }
}
