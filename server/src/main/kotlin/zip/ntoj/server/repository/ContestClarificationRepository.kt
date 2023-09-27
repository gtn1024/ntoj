package zip.ntoj.server.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import zip.ntoj.server.model.ContestClarification

interface ContestClarificationRepository :
    JpaRepository<ContestClarification, Long>,
    JpaSpecificationExecutor<ContestClarification> {
    fun findAllByContestContestId(contestId: Long): List<ContestClarification>
}
