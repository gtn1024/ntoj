package zip.ntoj.server.service

import org.springframework.stereotype.Service
import zip.ntoj.server.exception.AppException
import zip.ntoj.server.model.Submission
import zip.ntoj.server.repository.SubmissionRepository
import zip.ntoj.shared.model.JudgeStage
import kotlin.jvm.optionals.getOrNull

interface SubmissionService {
    fun get(id: Long): Submission
    fun getPendingSubmission(): Submission?
    fun new(submission: Submission): Submission
    fun update(submission: Submission): Submission
}

@Service
class SubmissionServiceImpl(
    val submissionRepository: SubmissionRepository,
) : SubmissionService {
    override fun get(id: Long): Submission {
        return submissionRepository.findById(id).orElseThrow { AppException("提交不存在", 404) }
    }

    override fun getPendingSubmission(): Submission? {
        return submissionRepository.findFirstByJudgeStageOrderBySubmissionId(JudgeStage.PENDING)
            .getOrNull()
    }

    override fun new(submission: Submission): Submission {
        return submissionRepository.save(submission)
    }

    override fun update(submission: Submission): Submission {
        return submissionRepository.save(submission)
    }
}
