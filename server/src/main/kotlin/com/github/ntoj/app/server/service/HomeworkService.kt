package com.github.ntoj.app.server.service

import com.github.ntoj.app.server.exception.AppException
import com.github.ntoj.app.server.model.entities.Homework
import com.github.ntoj.app.server.repository.HomeworkRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction.ASC
import org.springframework.data.domain.Sort.Direction.DESC
import org.springframework.stereotype.Service

interface HomeworkService {
    fun create(homework: Homework): Homework

    fun get(id: Long): Homework

    fun get(
        page: Int = 1,
        pageSize: Int = Int.MAX_VALUE,
        desc: Boolean = false,
    ): List<Homework>

    fun count(): Long

    fun update(homework: Homework): Homework

    fun remove(id: Long)
}

@Service
class HomeworkServiceImpl(
    val homeworkRepository: HomeworkRepository,
) : HomeworkService {
    override fun create(homework: Homework): Homework {
        return homeworkRepository.save(homework)
    }

    override fun get(id: Long): Homework {
        return homeworkRepository.findById(id).orElseThrow { AppException("作业不存在", 404) }
    }

    override fun get(
        page: Int,
        pageSize: Int,
        desc: Boolean,
    ): List<Homework> {
        return homeworkRepository.findAll(
            PageRequest.of(
                page - 1,
                pageSize,
                Sort.by(if (desc) DESC else ASC, "homeworkId"),
            ),
        ).toList()
    }

    override fun count(): Long {
        return homeworkRepository.count()
    }

    override fun update(homework: Homework): Homework {
        return homeworkRepository.save(homework)
    }

    override fun remove(id: Long) {
        homeworkRepository.deleteById(id)
    }
}
