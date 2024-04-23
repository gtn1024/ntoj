package com.github.ntoj.app.server.model.dtos.admin

import com.github.ntoj.app.server.model.dtos.UserDto
import com.github.ntoj.app.server.model.entities.Group
import java.io.Serializable
import java.time.Instant

data class GroupDto(
    val createdAt: Instant,
    val name: String,
    val users: List<UserDto>,
    val id: Long,
) : Serializable {
    companion object {
        fun from(group: Group) =
            GroupDto(
                createdAt = group.createdAt!!,
                name = group.name,
                users = group.users.map { UserDto.from(it) }.toMutableList(),
                id = group.groupId!!,
            )
    }
}
