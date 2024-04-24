package com.github.ntoj.app.server.model.dtos

import com.github.ntoj.app.server.model.entities.Group
import com.github.ntoj.app.server.model.entities.User
import com.github.ntoj.app.server.model.entities.UserRole
import java.io.Serializable
import java.time.Instant

data class UserDto(
    val createdAt: Instant,
    val username: String,
    val email: String? = null,
    val realName: String? = null,
    val bio: String? = null,
    val id: Long,
    val groups: List<UserGroupDto> = emptyList(),
    val role: UserRole = UserRole.USER,
) : Serializable {
    companion object {
        fun from(user: User) =
            UserDto(
                user.createdAt!!,
                user.username,
                user.email,
                user.realName,
                user.bio,
                user.userId!!,
                user.groups.map { UserGroupDto.from(it) },
                user.role,
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
