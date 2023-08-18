package zip.ntoj.server.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.HandlerInterceptor
import zip.ntoj.server.service.JudgeClientTokenService

@Configuration
class JudgeClientTokenInterceptor(
    val judgeClientTokenService: JudgeClientTokenService,
) : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val token = request.getHeader("X-Judger-Token") ?: return false
        if (!judgeClientTokenService.exists(token)) {
            // 403
            response.status = 403
            return false
        }
        return true
    }
}
