package com.github.ntoj.app.server.model.dtos

import com.github.ntoj.app.server.model.entities.Group
import com.github.ntoj.app.server.model.entities.Homework
import com.github.ntoj.app.server.model.entities.User
import java.io.Serializable
import java.time.Instant

data class GroupDto(
    val name: String,
    val users: List<GroupUserDto>,
    val homeworks: List<GroupHomeworkDto>,
    val id: Long,
) : Serializable {
    companion object {
        fun from(group: Group) =
            GroupDto(
                name = group.name,
                users = group.users.map { GroupUserDto.from(it) },
                homeworks = group.homeworks.map { GroupHomeworkDto.from(it) },
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

    data class GroupHomeworkDto(
        val id: Long,
        val title: String,
        val startTime: Instant,
        val endTime: Instant,
    ) {
        companion object {
            fun from(homework: Homework) =
                GroupHomeworkDto(
                    id = homework.homeworkId!!,
                    title = homework.title,
                    startTime = homework.startTime,
                    endTime = homework.endTime,
                )
        }
    }
}
