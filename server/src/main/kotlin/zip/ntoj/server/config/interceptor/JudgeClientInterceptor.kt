package zip.ntoj.server.config.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.HandlerInterceptor
import zip.ntoj.server.model.JudgerSystemStatus
import zip.ntoj.server.service.JudgeClientTokenService
import zip.ntoj.server.service.JudgerSystemStatusService
import java.time.Instant

@Configuration
class JudgeClientInterceptor(
    val judgeClientTokenService: JudgeClientTokenService,
    val judgerSystemStatusService: JudgerSystemStatusService,
) : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val token = request.getHeader("X-Judger-Token") ?: return false
        if (!judgeClientTokenService.exists(token)) {
            // 403
            response.status = 403
            return false
        }

        val id = request.getHeader("X-Judger-ID") ?: null
        val os = request.getHeader("X-Judger-OS") ?: null
        val kernel = request.getHeader("X-Judger-Kernel") ?: null
        val memoryUsed = request.getHeader("X-Judger-Memory-Used").toLongOrNull()
        val memoryTotal = request.getHeader("X-Judger-Memory-Total").toLongOrNull()
        if (id != null) {
            val judger = judgerSystemStatusService.findByJudgerId(id)
            if (judger == null) {
                judgerSystemStatusService.new(JudgerSystemStatus(id, os, kernel, memoryUsed, memoryTotal))
            } else if (judger.updatedAt == null || judger.updatedAt!! < Instant.now().minusSeconds(60)) {
                judger.os = os
                judger.kernel = kernel
                judger.memoryUsed = memoryUsed
                judger.memoryTotal = memoryTotal
                judgerSystemStatusService.update(judger)
            }
        }
        return true
    }
}
