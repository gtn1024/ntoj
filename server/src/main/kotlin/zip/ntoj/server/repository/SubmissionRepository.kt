package zip.ntoj.server.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import zip.ntoj.server.model.Submission

interface SubmissionRepository : JpaRepository<Submission, Long>, JpaSpecificationExecutor<Submission>
