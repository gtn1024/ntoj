package com.github.ntoj.app.server.repository

import com.github.ntoj.app.server.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.Optional

interface UserRepository : JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    fun existsByUsername(username: String): Boolean

    fun findByUsername(username: String): Optional<User>
}
