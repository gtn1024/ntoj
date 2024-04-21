package com.github.ntoj.app.server.controller

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.SaLoginConfig
import cn.dev33.satoken.stp.StpUtil
import com.fasterxml.jackson.annotation.JsonFormat
import com.github.ntoj.app.server.config.SecurityConfig
import com.github.ntoj.app.server.exception.AppException
import com.github.ntoj.app.server.ext.success
import com.github.ntoj.app.server.model.entities.User
import com.github.ntoj.app.server.model.entities.UserRole
import com.github.ntoj.app.server.service.UserService
import com.github.ntoj.app.server.util.checkPassword
import com.github.ntoj.app.server.util.getSalt
import com.github.ntoj.app.server.util.hashPassword
import com.github.ntoj.app.shared.model.R
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
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
            LoginResponse(userLogin(username, password, user), CurrentUserDto.from(user)),
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
                realName = request.realName,
                bio = request.bio,
                role = if (userService.count() == 0L) UserRole.SUPER_ADMIN else UserRole.USER,
            ),
        )
        return R.success(200, "注册成功")
    }

    @GetMapping("/current")
    @SaCheckLogin
    fun getCurrentUser(): ResponseEntity<R<CurrentUserDto>> {
        return R.success(200, "获取成功", CurrentUserDto.from(userService.getUserById(StpUtil.getLoginIdAsLong())))
    }
}

data class UserRequest(
    @field:NotEmpty(message = "用户名不能为空") val username: String,
    @field:NotEmpty(message = "密码不能为空") val password: String?,
    @field:NotEmpty(message = "邮箱不能为空") val email: String?,
    @field:NotEmpty(message = "姓名不能为空") val realName: String?,
    @field:Size(max = 100, message = "签名长度最多为100") val bio: String?,
)

data class LoginResponse(
    val token: String,
    val user: CurrentUserDto,
)

data class CurrentUserDto(
    val id: Long? = null,
    val username: String? = null,
    val email: String? = null,
    val realName: String? = null,
    val bio: String? = null,
    val role: UserRole? = UserRole.USER,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    val registerAt: Instant? = null,
) {
    companion object {
        fun from(user: User): CurrentUserDto {
            return CurrentUserDto(
                id = user.userId,
                username = user.username,
                email = user.email,
                realName = user.realName,
                bio = user.bio,
                role = user.role,
                registerAt = user.createdAt,
            )
        }
    }
}
