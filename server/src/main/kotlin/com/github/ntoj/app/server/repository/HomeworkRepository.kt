package com.github.ntoj.app.server.repository

import com.github.ntoj.app.server.model.entities.Group
import com.github.ntoj.app.server.model.entities.Homework
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface CustomHomeworkRepository {
    fun deleteGroupFromHomeworks(group: Group): Int
}

interface HomeworkRepository : JpaRepository<Homework, Long>, JpaSpecificationExecutor<Homework>, CustomHomeworkRepository {
    fun findAllByGroupsContains(group: Group): List<Homework>
}

class CustomHomeworkRepositoryImpl(
    @PersistenceContext private val entityManager: EntityManager,
) : CustomHomeworkRepository {
    override fun deleteGroupFromHomeworks(group: Group): Int {
        // delete from t_homeworks_groups where group_id=?
        return entityManager.createNativeQuery("delete from t_homeworks_groups where group_id=?")
            .setParameter(1, group.groupId)
            .executeUpdate()
    }
}
