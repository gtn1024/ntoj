package zip.ntoj.server.controller

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import zip.ntoj.server.ext.success
import zip.ntoj.server.model.User
import zip.ntoj.server.service.UserService
import zip.ntoj.shared.model.R
import java.time.Instant

@RestController
@RequestMapping("/user")
class UserController(
    val userService: UserService,
) {
    @GetMapping("/{username}")
    fun getUser(
        @PathVariable username: String,
    ): ResponseEntity<R<UserDto>> {
        val user = userService.getUserByUsername(username)
        return R.success(200, "获取成功", UserDto.from(user))
    }

    @PatchMapping
    @SaCheckLogin
    fun updateUser(
        @RequestBody @Valid
        userUpdateRequest: UserUpdateRequest,
    ): ResponseEntity<R<Void>> {
        val user = userService.getUserById(StpUtil.getLoginIdAsLong())
        if (userUpdateRequest.bio != null) {
            user.bio = userUpdateRequest.bio
        }
        userService.updateUser(user)
        return R.success(200, "修改成功")
    }

    data class UserDto(
        val id: Long? = null,
        val username: String? = null,
        val realName: String? = null,
        val bio: String? = null,
        @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
        val registerAt: Instant? = null,
    ) {
        companion object {
            fun from(user: User): UserDto {
                return UserDto(
                    id = user.userId,
                    username = user.username,
                    realName = user.realName,
                    bio = user.bio,
                    registerAt = user.createdAt,
                )
            }
        }
    }
}

data class UserUpdateRequest(
    @field:Size(max = 100, message = "签名长度最多为100") val bio: String?,
)
