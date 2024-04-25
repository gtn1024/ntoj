package com.github.ntoj.app.server.model.dtos

import com.github.ntoj.app.server.model.entities.Group
import com.github.ntoj.app.server.model.entities.User
import java.io.Serializable

data class GroupDto(
    val name: String,
    val users: List<GroupUserDto>,
    val id: Long,
) : Serializable {
    companion object {
        fun from(group: Group) =
            GroupDto(
                name = group.name,
                users = group.users.map { GroupUserDto.from(it) },
                id = group.groupId!!,
            )
    }

    data class GroupUserDto(
        val username: String,
        val realName: String?,
        val id: Long,
    ) : Serializable {
        companion object {
            fun from(user: User) = GroupUserDto(user.username, user.realName, user.userId!!)
        }
    }
}
