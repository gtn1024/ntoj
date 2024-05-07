package com.github.ntoj.app.server.controller

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.SaLoginConfig
import cn.dev33.satoken.stp.StpUtil
import com.github.ntoj.app.server.config.SecurityConfig
import com.github.ntoj.app.server.exception.AppException
import com.github.ntoj.app.server.ext.success
import com.github.ntoj.app.server.model.dtos.UserDto
import com.github.ntoj.app.server.model.entities.Group
import com.github.ntoj.app.server.model.entities.User
import com.github.ntoj.app.server.model.entities.UserRole
import com.github.ntoj.app.server.service.UserService
import com.github.ntoj.app.server.util.checkPassword
import com.github.ntoj.app.server.util.getSalt
import com.github.ntoj.app.server.util.hashPassword
import com.github.ntoj.app.shared.model.R
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.Serializable
import java.time.Instant

@RestController
@RequestMapping("/auth")
class AuthController(
    val userService: UserService,
    val securityConfig: SecurityConfig,
) {
    @GetMapping("/login")
    fun login(
        @RequestParam username: String,
        @RequestParam password: String,
    ): ResponseEntity<R<LoginResponse>> {
        val user = userService.getUserByUsername(username)
        return R.success(
            200,
            "登录成功",
            LoginResponse(userLogin(username, password, user), UserDto.from(user)),
        )
    }

    private fun userLogin(
        username: String,
        password: String,
        user: User,
    ): String {
        if (!checkPassword(password, user.salt!!, user.password!!)) {
            throw AppException("密码错误", 401)
        }
        StpUtil.login(user.userId, SaLoginConfig.setTimeout(securityConfig.tokenExpireTime))
        return StpUtil.getTokenInfo().getTokenValue()
    }

    @PostMapping("/signup")
    fun signup(
        @Valid @RequestBody
        request: UserRequest,
    ): ResponseEntity<R<Void>> {
        // 创建用户
        val salt = getSalt()
        userService.newUser(
            User(
                username = request.username,
                password = hashPassword(request.password!!, salt),
                salt = salt,
                email = request.email,
                role = if (userService.count() == 0L) UserRole.SUPER_ADMIN else UserRole.USER,
            ),
        )
        return R.success(200, "注册成功")
    }

    @GetMapping("/current")
    @SaCheckLogin
    fun getCurrentUser(): ResponseEntity<R<CurrentUser>> {
        val user = userService.getUserById(StpUtil.getLoginIdAsLong())
        val groups = userService.getUserGroups(user)
        return R.success(200, "获取成功", CurrentUser.from(user, groups))
    }
}

data class CurrentUser(
    val createdAt: Instant,
    val username: String,
    val email: String? = null,
    val displayName: String? = null,
    val bio: String? = null,
    val id: Long,
    val role: UserRole = UserRole.USER,
    val groups: List<UserGroupDto> = emptyList(),
) : Serializable {
    companion object {
        fun from(
            user: User,
            groups: List<Group>,
        ) = CurrentUser(
            user.createdAt!!,
            user.username,
            user.email,
            user.displayName,
            user.bio,
            user.userId!!,
            user.role,
            groups.map { UserGroupDto.from(it) },
        )
    }

    data class UserGroupDto(
        val id: Long,
        val name: String,
        val userNumber: Int,
    ) {
        companion object {
            fun from(group: Group) =
                UserGroupDto(
                    group.groupId!!,
                    group.name,
                    group.users.size,
                )
        }
    }
}

data class UserRequest(
    @field:NotEmpty(message = "用户名不能为空") val username: String,
    @field:NotEmpty(message = "密码不能为空") val password: String?,
    @field:NotEmpty(message = "邮箱不能为空") val email: String?,
)

data class LoginResponse(
    val token: String,
    val user: UserDto,
)
