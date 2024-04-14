package zip.ntoj.server.config

import cn.dev33.satoken.stp.StpInterface
import org.springframework.stereotype.Component
import zip.ntoj.server.model.UserRole
import zip.ntoj.server.service.UserService

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
