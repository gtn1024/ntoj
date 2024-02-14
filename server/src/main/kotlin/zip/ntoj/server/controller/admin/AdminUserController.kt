package zip.ntoj.server.controller.admin

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.annotation.SaMode
import cn.dev33.satoken.stp.StpUtil
import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import zip.ntoj.server.exception.AppException
import zip.ntoj.server.ext.fail
import zip.ntoj.server.ext.success
import zip.ntoj.server.model.Contest
import zip.ntoj.server.model.ContestUser
import zip.ntoj.server.model.L
import zip.ntoj.server.model.User
import zip.ntoj.server.model.UserRole
import zip.ntoj.server.service.UserService
import zip.ntoj.server.util.getSalt
import zip.ntoj.server.util.hashPassword
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

    @GetMapping("{id}")
    fun get(@PathVariable id: Long): ResponseEntity<R<AdminUserDto>> {
        val user = userService.getUserById(id)
        return R.success(200, "获取成功", AdminUserDto.from(user))
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<R<Void>> {
        if (!userService.existsById(id)) return R.fail(404, "用户不存在")
        userService.deleteUserById(id)
        return R.success(200, "删除成功")
    }

    @PostMapping
    fun add(@RequestBody request: UserRequest): ResponseEntity<R<AdminUserDto>> {
        requireNotNull(request.username) { "用户名不能为空" }
        requireNotNull(request.password) { "密码不能为空" }
        requireNotNull(request.realName) { "真实姓名不能为空" }
        requireNotNull(request.email) { "邮箱不能为空" }
        requireNotNull(request.role) { "角色不能为空" }
        val salt = getSalt()
        val user = User(
            username = request.username,
            password = hashPassword(request.password, salt),
            salt = salt,
            email = request.email,
            realName = request.realName,
            role = request.role,
            bio = null,
        )
        userService.newUser(user)
        return R.success(200, "添加成功", AdminUserDto.from(user))
    }

    @PatchMapping("{id}")
    fun update(
        @RequestBody request: UserRequest,
        @PathVariable id: Long
    ): ResponseEntity<R<AdminUserDto>> {
        val user = userService.getUserById(id)
        if (request.username != null) {
            user.username = request.username
        }
        if (request.password != null) {
            val salt = getSalt()
            user.password = hashPassword(request.password, salt)
            user.salt = salt
        }
        if (request.email != null) {
            user.email = request.email
        }
        if (request.realName != null) {
            user.realName = request.realName
        }
        if (request.role != null) {
            user.role = request.role
        }
        userService.updateUser(user)
        return R.success(200, "更新成功", AdminUserDto.from(user))
    }

    data class UserRequest(
        val username: String?,
        val password: String?,
        val email: String?,
        val realName: String?,
        val role: UserRole?,
    )
}
