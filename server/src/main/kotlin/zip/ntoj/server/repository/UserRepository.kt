package zip.ntoj.server.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import zip.ntoj.server.model.User
import java.util.Optional

interface UserRepository : JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    fun existsByUsername(username: String): Boolean
    fun findByUsername(username: String): Optional<User>
}
