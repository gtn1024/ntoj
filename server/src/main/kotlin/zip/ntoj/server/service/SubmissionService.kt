package zip.ntoj.server.service

import jakarta.persistence.criteria.Join
import jakarta.persistence.criteria.Predicate
import jakarta.transaction.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import zip.ntoj.server.exception.AppException
import zip.ntoj.server.model.Problem
import zip.ntoj.server.model.Submission
import zip.ntoj.server.repository.SubmissionRepository
import zip.ntoj.server.service.SubmissionService.SubmissionScope
import zip.ntoj.shared.model.JudgeStage
import kotlin.jvm.optionals.getOrNull

interface SubmissionService {
    enum class SubmissionScope {
        ALL,
        PROBLEM,
        CONTEST,
    }

    fun get(
        onlyVisibleProblem: Boolean = false,
        scope: SubmissionScope = SubmissionScope.ALL,
        page: Int = 1,
        pageSize: Int = Int.MAX_VALUE,
        desc: Boolean = false,
    ): List<Submission>

    fun count(
        onlyVisibleProblem: Boolean = false,
        scope: SubmissionScope = SubmissionScope.ALL,
    ): Long

    fun get(
        id: Long,
        onlyVisibleProblem: Boolean = false,
    ): Submission

    fun getByContestId(
        contestId: Long,
        page: Int = 1,
        pageSize: Int = Int.MAX_VALUE,
        desc: Boolean = false,
        username: String? = null,
    ): List<Submission>

    fun countByContestId(
        contestId: Long,
        username: String? = null,
    ): Long

    fun getPendingSubmissionAndSetJudging(): Submission?

    fun new(submission: Submission): Submission

    fun update(submission: Submission): Submission
}

@Service
class SubmissionServiceImpl(
    val submissionRepository: SubmissionRepository,
) : SubmissionService {
    override fun get(
        onlyVisibleProblem: Boolean,
        scope: SubmissionScope,
        page: Int,
        pageSize: Int,
        desc: Boolean,
    ): List<Submission> {
        return submissionRepository.findAll(
            buildSpecification(onlyVisibleProblem, scope),
            PageRequest.of(
                page - 1,
                pageSize,
                Sort.by(if (desc) Sort.Direction.DESC else Sort.Direction.ASC, "submissionId"),
            ),
        ).toList()
    }

    private fun buildSpecification(
        onlyVisibleProblem: Boolean,
        scope: SubmissionScope = SubmissionScope.ALL,
    ): Specification<Submission> {
        return Specification { root, _, cb ->
            val problemJoin: Join<Submission, Problem> = root.join("problem")
            val predicates: MutableList<Predicate> = mutableListOf()
            predicates.add(
                if (onlyVisibleProblem) {
                    cb.isTrue(problemJoin.get("visible"))
                } else {
                    cb.isFalse(problemJoin.get("visible"))
                },
            )
            if (scope != SubmissionScope.ALL) {
                predicates.add(
                    when (scope) {
                        SubmissionScope.PROBLEM ->
                            cb.equal(
                                root.get<Enum<Submission.SubmissionOrigin>>("origin"),
                                Submission.SubmissionOrigin.PROBLEM,
                            )

                        SubmissionScope.CONTEST ->
                            cb.equal(
                                root.get<Enum<Submission.SubmissionOrigin>>("origin"),
                                Submission.SubmissionOrigin.CONTEST,
                            )

                        else -> throw AppException("未知的 scope", 500)
                    },
                )
            }
            return@Specification cb.and(*predicates.toTypedArray())
        }
    }

    override fun get(
        id: Long,
        onlyVisibleProblem: Boolean,
    ): Submission {
        val submission = submissionRepository.findById(id).orElseThrow { AppException("提交不存在", 404) }
        if (submission.problem?.visible != true && submission.contestId == null) {
            throw AppException("提交不存在", 404)
        }
        return submission
    }

    override fun count(
        onlyVisibleProblem: Boolean,
        scope: SubmissionScope,
    ): Long {
        return submissionRepository.count(buildSpecification(onlyVisibleProblem, scope))
    }

    override fun getByContestId(
        contestId: Long,
        page: Int,
        pageSize: Int,
        desc: Boolean,
        username: String?,
    ): List<Submission> {
        return submissionRepository.findAll(
            Specification { root, _, cb ->
                val predicates: MutableList<Predicate> = mutableListOf()
                predicates.add(cb.equal(root.get<Long>("contestId"), contestId))
                if (username != null) {
                    predicates.add(cb.equal(root.get<Submission>("user").get<String>("username"), username))
                }
                return@Specification cb.and(*predicates.toTypedArray())
            },
            PageRequest.of(
                page - 1,
                pageSize,
                Sort.by(if (desc) Sort.Direction.DESC else Sort.Direction.ASC, "submissionId"),
            ),
        ).toList()
    }

    override fun countByContestId(
        contestId: Long,
        username: String?,
    ): Long {
        return submissionRepository.count(
            Specification { root, _, cb ->
                val predicates: MutableList<Predicate> = mutableListOf()
                predicates.add(cb.equal(root.get<Long>("contestId"), contestId))
                if (username != null) {
                    predicates.add(cb.equal(root.get<Submission>("user").get<String>("username"), username))
                }
                return@Specification cb.and(*predicates.toTypedArray())
            },
        )
    }

    @Transactional
    override fun getPendingSubmissionAndSetJudging(): Submission? {
        var submission =
            submissionRepository.findFirstByJudgeStageOrderBySubmissionId(JudgeStage.PENDING)
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
