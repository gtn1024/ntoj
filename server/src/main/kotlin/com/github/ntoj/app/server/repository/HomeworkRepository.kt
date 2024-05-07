package com.github.ntoj.app.server.repository

import com.github.ntoj.app.server.model.entities.Group
import com.github.ntoj.app.server.model.entities.Homework
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface HomeworkRepository : JpaRepository<Homework, Long>, JpaSpecificationExecutor<Homework> {
    fun findAllByGroupsContains(group: Group): List<Homework>
}
