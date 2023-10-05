package zip.ntoj.server.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import zip.ntoj.server.exception.AppException
import zip.ntoj.server.model.SelfTestSubmission
import zip.ntoj.server.repository.SelfTestSubmissionRepository
import zip.ntoj.shared.model.JudgeStage
import kotlin.jvm.optionals.getOrNull

interface SelfTestSubmissionService {
    fun get(id: Long): SelfTestSubmission
    fun add(selfTestSubmission: SelfTestSubmission)
    fun getPendingSubmissionAndSetJudging(): SelfTestSubmission?
    fun update(submission: SelfTestSubmission)
}

@Service
class SelfTestSubmissionServiceImpl(
    private val selfTestSubmissionRepository: SelfTestSubmissionRepository,
) : SelfTestSubmissionService {
    override fun get(id: Long): SelfTestSubmission {
        return selfTestSubmissionRepository.findById(id).orElseThrow { AppException("自测提交不存在", 404) }
    }

    override fun add(selfTestSubmission: SelfTestSubmission) {
        selfTestSubmissionRepository.save(selfTestSubmission)
    }

    @Transactional
    override fun getPendingSubmissionAndSetJudging(): SelfTestSubmission? {
        var submission =
            selfTestSubmissionRepository.findFirstByJudgeStageOrderBySelfTestSubmissionId(JudgeStage.PENDING)
                .getOrNull()
        if (submission != null) {
            submission.judgeStage = JudgeStage.JUDGING
            submission = selfTestSubmissionRepository.save(submission)
        }
        return submission
    }

    override fun update(submission: SelfTestSubmission) {
        selfTestSubmissionRepository.save(submission)
    }
}
