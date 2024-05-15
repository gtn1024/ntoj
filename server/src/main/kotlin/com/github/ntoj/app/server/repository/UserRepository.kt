package com.github.ntoj.app.server.repository

import com.github.ntoj.app.server.model.entities.User
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.Optional

interface CustomUserRepository {
    /**
     * Set the role of all users with the specified role to 'default'.
     */
    fun setDefaultRoleForUsersWithRole(role: String): Int
}

interface UserRepository : JpaRepository<User, Long>, JpaSpecificationExecutor<User>, CustomUserRepository {
    fun existsByUsername(username: String): Boolean

    fun findByUsername(username: String): Optional<User>
}

class CustomUserRepositoryImpl(
    @PersistenceContext private val entityManager: EntityManager,
) : CustomUserRepository {
    override fun setDefaultRoleForUsersWithRole(role: String): Int {
        val sql = "update t_users set role='default' where role=?"
        return entityManager.createNativeQuery(sql)
            .setParameter(1, role)
            .executeUpdate()
    }
}
