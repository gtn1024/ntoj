package zip.ntoj.server.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import zip.ntoj.server.model.JudgeClientToken
import java.util.Optional

interface JudgeClientTokenRepository :
    JpaRepository<JudgeClientToken, Long>,
    JpaSpecificationExecutor<JudgeClientToken> {
    fun findByToken(token: String): Optional<JudgeClientToken>
}
