package zip.ntoj.server.controller.admin

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.annotation.SaMode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import zip.ntoj.server.ext.success
import zip.ntoj.server.model.L
import zip.ntoj.server.model.User
import zip.ntoj.server.service.UserService
import zip.ntoj.shared.model.R

@RestController
@RequestMapping("/admin/user")
@SaCheckLogin
@SaCheckRole(value = ["COACH", "ADMIN", "SUPER_ADMIN"], mode = SaMode.OR)
class AdminUserController(
    private val userService: UserService,
) {
    @GetMapping
    fun index(
        @RequestParam(required = false, defaultValue = "1") current: Int,
        @RequestParam(required = false, defaultValue = "20") pageSize: Int,
    ): ResponseEntity<R<L<UserDto>>> {
        val list = userService.get(page = current, pageSize = pageSize)
        val count = userService.count()
        return R.success(
            200,
            "获取成功",
            L(
                total = count,
                page = current,
                list = list.map { UserDto.from(it) },
            ),
        )
    }

    data class UserDto(
        val id: Long,
    ) {
        companion object {
            fun from(user: User): UserDto {
                return UserDto(
                    id = user.userId!!,
                )
            }
        }
    }
}
