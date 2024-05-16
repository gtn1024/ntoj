package com.github.ntoj.app.server.controller.admin

import cn.dev33.satoken.annotation.SaCheckPermission
import com.fasterxml.jackson.annotation.JsonFormat
import com.github.ntoj.app.server.exception.AppException
import com.github.ntoj.app.server.ext.success
import com.github.ntoj.app.server.model.L
import com.github.ntoj.app.server.model.entities.User
import com.github.ntoj.app.server.model.entities.UserRole
import com.github.ntoj.app.server.service.PermissionRoleService
import com.github.ntoj.app.server.service.UserService
import com.github.ntoj.app.server.util.getSalt
import com.github.ntoj.app.server.util.hashPassword
import com.github.ntoj.app.shared.model.R
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/admin/user")
class AdminUserController(
    private val userService: UserService,
    private val permissionRoleService: PermissionRoleService,
) {
    data class AdminUserDto(
        val id: Long,
        val username: String,
        val email: String?,
        val displayName: String?,
        val role: UserRole,
        val userRole: String,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") val createdAt: Instant,
    ) {
        companion object {
            fun from(user: User) =
                AdminUserDto(
                    user.userId!!,
                    user.username,
                    user.email,
                    user.displayName,
                    user.role,
                    user.userRole,
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
    fun get(
        @PathVariable id: Long,
    ): ResponseEntity<R<AdminUserDto>> {
        val user = userService.getUserById(id)
        return R.success(200, "获取成功", AdminUserDto.from(user))
    }

    @GetMapping("search")
    fun search(
        @RequestParam keyword: String,
    ): ResponseEntity<R<List<AdminUserDto>>> {
        val users = userService.search(keyword)
        return R.success(200, "获取成功", users.map { AdminUserDto.from(it) })
    }

    @PostMapping
    @SaCheckPermission(value = ["PERM_REGISTER_USER"])
    fun add(
        @RequestBody request: UserRequest,
    ): ResponseEntity<R<AdminUserDto>> {
        requireNotNull(request.username) { "用户名不能为空" }
        requireNotNull(request.password) { "密码不能为空" }
        requireNotNull(request.displayName) { "显示名不能为空" }
        requireNotNull(request.email) { "邮箱不能为空" }
        requireNotNull(request.role) { "角色不能为空" }
        val salt = getSalt()
        val user =
            User(
                username = request.username,
                password = hashPassword(request.password, salt),
                salt = salt,
                email = request.email,
                displayName = request.displayName,
                role = request.role,
                bio = null,
            )
        userService.newUser(user)
        return R.success(200, "添加成功", AdminUserDto.from(user))
    }

    @PatchMapping("{id}/setRole")
    @SaCheckPermission(value = ["PERM_SET_PERM"])
    fun setUserRole(
        @PathVariable id: Long,
        @RequestParam role: String,
    ): ResponseEntity<R<Unit>> {
        val user = userService.getUserById(id)
        val userRole =
            when (role) {
                "root" -> "root"
                "default" -> "default"
                "guest" -> "default"
                else -> if (permissionRoleService.get(role) == null) "default" else role
            }
        user.userRole = userRole
        userService.updateUser(user)
        return R.success(200, "设置成功")
    }

    @PostMapping("user_import_preview")
    fun userImportPreview(
        @RequestBody request: UserImportRequest,
    ): ResponseEntity<R<List<UserPreviewDto>>> {
        val users = getImportUsers(request.users)
        val userPreview =
            users.map {
                UserPreviewDto(
                    it.username,
                    it.password!!,
                    it.displayName!!,
                    it.email!!,
                    it.role,
                    userService.existsByUsername(it.username),
                )
            }
        return R.success(200, "获取成功", userPreview)
    }

    @PostMapping("user_import")
    @SaCheckPermission(value = ["PERM_REGISTER_USER"])
    fun userImport(
        @RequestBody request: UserImportRequest,
    ): ResponseEntity<R<Void>> {
        val users = getImportUsers(request.users)
        users.forEach { user ->
            user.salt = getSalt()
            user.password = hashPassword(user.password!!, user.salt!!)
            if (!userService.existsByUsername(user.username)) {
                userService.newUser(user)
            }
        }
        return R.success(200, "导入成功")
    }

    data class UserImportRequest(
        val users: String,
    )

    private fun getImportUsers(s: String): List<User> {
        return s.trim().split("\n").map {
            val split = it.split("\t")
            if (split.size != 5) throw AppException("格式错误", 400)
            User(
                username = split[0],
                password = split[1],
                email = split[2],
                displayName = split[3],
                role = UserRole.valueOf(split[4]),
                salt = getSalt(),
            )
        }
    }

    data class UserPreviewDto(
        val username: String,
        val password: String,
        val displayName: String,
        val email: String,
        val role: UserRole,
        val exists: Boolean,
    )

    data class UserRequest(
        val username: String?,
        val password: String?,
        val email: String?,
        val displayName: String?,
        val role: UserRole?,
    )
}
