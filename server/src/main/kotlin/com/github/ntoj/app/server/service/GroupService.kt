package com.github.ntoj.app.server.service

import com.github.ntoj.app.server.exception.AppException
import com.github.ntoj.app.server.model.entities.Group
import com.github.ntoj.app.server.repository.GroupRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction.ASC
import org.springframework.data.domain.Sort.Direction.DESC
import org.springframework.stereotype.Service

interface GroupService {
    fun create(group: Group): Group

    fun get(id: Long): Group

    fun get(
        page: Int = 1,
        pageSize: Int = Int.MAX_VALUE,
        desc: Boolean = false,
    ): List<Group>

    fun count(): Long

    fun update(group: Group): Group

    fun remove(id: Long)
}

@Service
class GroupServiceImpl(private val groupRepository: GroupRepository) : GroupService {
    override fun create(group: Group): Group {
        return groupRepository.save(group)
    }

    override fun get(id: Long): Group {
        return groupRepository.findById(id).orElseThrow { AppException("小组不存在", 404) }
    }

    override fun get(
        page: Int,
        pageSize: Int,
        desc: Boolean,
    ): List<Group> {
        return groupRepository.findAll(
            PageRequest.of(
                page - 1,
                pageSize,
                Sort.by(if (desc) DESC else ASC, "groupId"),
            ),
        ).toList()
    }

    override fun count(): Long {
        return groupRepository.count()
    }

    override fun update(group: Group): Group {
        return groupRepository.save(group)
    }

    override fun remove(id: Long) {
        groupRepository.deleteById(id)
    }
}
