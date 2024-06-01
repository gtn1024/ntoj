package com.github.ntoj.app.server.config

import cn.dev33.satoken.stp.StpInterface
import com.github.ntoj.app.server.model.entities.PERM_GUEST
import com.github.ntoj.app.server.model.entities.Permission
import com.github.ntoj.app.server.service.PermissionRoleService
import com.github.ntoj.app.server.service.UserService
import org.springframework.stereotype.Component
import java.math.BigInteger

@Component
class StpInterfaceImpl(
    val userService: UserService,
    val permissionRoleService: PermissionRoleService,
) : StpInterface {
    override fun getPermissionList(
        loginId: Any?,
        loginType: String?,
    ): List<String> {
        val permission: BigInteger
        if (loginId == null || loginId == -1L) {
            permission = permissionRoleService.get("guest")?.permission ?: PERM_GUEST
        } else {
            val userId = loginId.toString().toLong()
            var role = userService.getUserById(userId).userRole
            if (role == "guest") {
                role = "default"
            }
            permission = permissionRoleService.getPermission(role)
        }
        val list = mutableListOf<String>()
        Permission.forEach {
            if ((permission and it.value) == it.value) {
                list += it.key
            }
        }
        return list
    }

    override fun getRoleList(
        loginId: Any?,
        loginType: String?,
    ): List<String> {
        if (loginId == null || loginId == -1L) {
            return listOf("guest")
        }
        val userId = loginId.toString().toLong()
        val user = userService.getUserById(userId)
        val list = listOf(user.userRole)
        return list
    }
}
