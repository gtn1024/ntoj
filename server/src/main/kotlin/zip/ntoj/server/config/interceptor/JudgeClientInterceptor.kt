package zip.ntoj.server.config.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.HandlerInterceptor
import zip.ntoj.server.model.JudgeClientToken
import zip.ntoj.server.service.JudgeClientTokenService
import java.time.Instant

@Configuration
class JudgeClientInterceptor(
    val judgeClientTokenService: JudgeClientTokenService,
) : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val token = request.getHeader("X-Judger-Token") ?: return false
        if (!judgeClientTokenService.exists(token)) {
            // 403
            response.status = 403
            return false
        }

        val os = request.getHeader("X-Judger-OS") ?: null
        val kernel = request.getHeader("X-Judger-Kernel") ?: null
        val memoryUsed = request.getHeader("X-Judger-Memory-Used").toLongOrNull()
        val memoryTotal = request.getHeader("X-Judger-Memory-Total").toLongOrNull()
        val judgeClientToken = judgeClientTokenService.get(token)
        if ((os != null && kernel != null && memoryUsed != null && memoryTotal != null) && isUpdatable(judgeClientToken)) {
            judgeClientTokenService.updateSystemInfo(token, os, kernel, memoryUsed, memoryTotal)
        }
        return true
    }

    private fun isUpdatable(judgeClientToken: JudgeClientToken): Boolean {
        return judgeClientToken.infoLastUpdatedAt == null || judgeClientToken.infoLastUpdatedAt!!.plusSeconds(60)
            .isBefore(Instant.now())
    }
}
