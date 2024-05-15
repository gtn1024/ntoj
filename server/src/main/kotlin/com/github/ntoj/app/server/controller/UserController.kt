package com.github.ntoj.app.server.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import cn.dev33.satoken.stp.StpUtil
import com.github.ntoj.app.server.ext.success
import com.github.ntoj.app.server.model.dtos.UserDto
import com.github.ntoj.app.server.service.UserService
import com.github.ntoj.app.shared.model.R
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
@SaCheckPermission(value = ["PERM_VIEW"])
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
    @SaCheckPermission(value = ["PERM_USER_PROFILE"])
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
}

data class UserUpdateRequest(
    @field:Size(max = 100, message = "签名长度最多为100") val bio: String?,
)
