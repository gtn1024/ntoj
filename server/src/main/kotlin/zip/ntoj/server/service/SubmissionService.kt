package zip.ntoj.server.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import zip.ntoj.server.exception.AppException
import zip.ntoj.server.model.Submission
import zip.ntoj.server.repository.SubmissionRepository
import zip.ntoj.shared.model.JudgeStage
import kotlin.jvm.optionals.getOrNull

interface SubmissionService {
    fun get(id: Long): Submission
    fun getPendingSubmissionAndSetJudging(): Submission?
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

    @Transactional
    override fun getPendingSubmissionAndSetJudging(): Submission? {
        var submission = submissionRepository.findFirstByJudgeStageOrderBySubmissionId(JudgeStage.PENDING)
            .getOrNull()
        if (submission != null) {
            submission.judgeStage = JudgeStage.JUDGING
            submission = submissionRepository.save(submission)
        }
        return submission
    }

    override fun new(submission: Submission): Submission {
        return submissionRepository.save(submission)
    }

    override fun update(submission: Submission): Submission {
        return submissionRepository.save(submission)
    }
}
