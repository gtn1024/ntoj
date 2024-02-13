package zip.ntoj.server.controller.admin

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.annotation.SaMode
import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import zip.ntoj.server.ext.success
import zip.ntoj.server.model.L
import zip.ntoj.server.model.User
import zip.ntoj.server.model.UserRole
import zip.ntoj.server.service.UserService
import zip.ntoj.shared.model.R
import java.time.Instant

@RestController
@RequestMapping("/admin/user")
@SaCheckLogin
@SaCheckRole(value = ["COACH", "ADMIN", "SUPER_ADMIN"], mode = SaMode.OR)
class AdminUserController(
    private val userService: UserService,
) {
    data class AdminUserDto(
        val id: Long,
        val username: String,
        val email: String?,
        val realName: String?,
        val role: UserRole,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") val createdAt: Instant,
    ) {
        companion object {
            fun from(user: User) = AdminUserDto(
                user.userId!!,
                user.username,
                user.email,
                user.realName,
                user.role,
                user.createdAt!!,
            )
        }
    }

    @GetMapping
    fun index(
        @RequestParam(required = false, defaultValue = "1") current: Int,
        @RequestParam(required = false, defaultValue = "20") pageSize: Int,
    ): ResponseEntity<R<L<*>>> {
        val list = userService.get(page = current, pageSize = pageSize)
        val count = userService.count()
        return R.success(
            200,
            "获取成功",
            L(total = count, page = current, list = list.map { AdminUserDto.from(it) }),
        )
    }
}
