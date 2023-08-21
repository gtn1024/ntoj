package zip.ntoj.server.service

import jakarta.persistence.criteria.Join
import jakarta.transaction.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import zip.ntoj.server.exception.AppException
import zip.ntoj.server.model.Problem
import zip.ntoj.server.model.Submission
import zip.ntoj.server.repository.SubmissionRepository
import zip.ntoj.shared.model.JudgeStage
import kotlin.jvm.optionals.getOrNull

interface SubmissionService {
    fun get(
        onlyVisibleProblem: Boolean = false,
        page: Int = 1,
        pageSize: Int = Int.MAX_VALUE,
        desc: Boolean = false,
    ): List<Submission>

    fun count(
        onlyVisibleProblem: Boolean = false,
    ): Long
    fun get(id: Long): Submission
    fun getPendingSubmissionAndSetJudging(): Submission?
    fun new(submission: Submission): Submission
    fun update(submission: Submission): Submission
}

@Service
class SubmissionServiceImpl(
    val submissionRepository: SubmissionRepository,
) : SubmissionService {
    override fun get(onlyVisibleProblem: Boolean, page: Int, pageSize: Int, desc: Boolean): List<Submission> {
        return submissionRepository.findAll(
            buildSpecification(onlyVisibleProblem),
            PageRequest.of(
                page - 1,
                pageSize,
                Sort.by(if (desc) Sort.Direction.DESC else Sort.Direction.ASC, "submissionId"),
            ),
        ).toList()
    }

    private fun buildSpecification(onlyVisibleProblem: Boolean): Specification<Submission> {
        return Specification { root, _, cb ->
            val problemJoin: Join<Submission, Problem> = root.join("problem")
            return@Specification if (onlyVisibleProblem) {
                cb.isTrue(problemJoin.get<Boolean>("visible"))
            } else {
                cb.isFalse(problemJoin.get<Boolean>("visible"))
            }
        }
    }


    override fun get(id: Long): Submission {
        return submissionRepository.findById(id).orElseThrow { AppException("提交不存在", 404) }
    }

    override fun count(onlyVisibleProblem: Boolean): Long {
        return submissionRepository.count(buildSpecification(onlyVisibleProblem))
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
