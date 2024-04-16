package com.github.ntoj.app.server.config

import cn.dev33.satoken.stp.StpInterface
import com.github.ntoj.app.server.model.UserRole
import com.github.ntoj.app.server.service.UserService
import org.springframework.stereotype.Component

@Component
class StpInterfaceImpl(
    val userService: UserService,
) : StpInterface {
    override fun getPermissionList(
        loginId: Any?,
        loginType: String?,
    ): List<String> {
        return listOf()
    }

    override fun getRoleList(
        loginId: Any?,
        loginType: String?,
    ): List<String> {
        val userId = loginId?.toString()?.toLong() ?: return listOf()
        val user = userService.getUserById(userId)
        val list = mutableListOf<String>()
        if (user.role.ordinal > 0) {
            list += UserRole.USER.name
        }
        if (user.role.ordinal > 1) {
            list += user.role.name
        }
        return list
    }
}
