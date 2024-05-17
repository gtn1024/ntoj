package com.github.ntoj.app.server.service

import com.github.ntoj.app.server.exception.AppException
import com.github.ntoj.app.server.model.entities.SelfTestSubmission
import com.github.ntoj.app.server.repository.SelfTestSubmissionRepository
import com.github.ntoj.app.shared.model.JudgeStage
import jakarta.persistence.criteria.Predicate
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

interface SelfTestSubmissionService {
    fun get(id: Long): SelfTestSubmission

    fun add(selfTestSubmission: SelfTestSubmission)

    fun update(submission: SelfTestSubmission)

    fun getPendingSubmissions(
        limit: Int,
        from: Long = 1,
    ): List<SelfTestSubmission>

    fun setJudging(selfTestSubmissionId: Long): SelfTestSubmission
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

    override fun update(submission: SelfTestSubmission) {
        selfTestSubmissionRepository.save(submission)
    }

    override fun getPendingSubmissions(
        limit: Int,
        from: Long,
    ): List<SelfTestSubmission> {
        return selfTestSubmissionRepository.findAll(
            Specification { root, _, cb ->
                val predicates: MutableList<Predicate> = mutableListOf()
                predicates.add(cb.equal(root.get<JudgeStage>("judgeStage"), JudgeStage.PENDING))
                predicates.add(cb.greaterThanOrEqualTo(root.get("selfTestSubmissionId"), from))
                return@Specification cb.and(*predicates.toTypedArray())
            },
            PageRequest.of(
                0,
                limit,
                Sort.by(Sort.Direction.ASC, "selfTestSubmissionId"),
            ),
        ).toList()
    }

    override fun setJudging(selfTestSubmissionId: Long): SelfTestSubmission {
        val submission = selfTestSubmissionRepository.findById(selfTestSubmissionId).orElseThrow { AppException("提交不存在", 404) }
        submission.judgeStage = JudgeStage.JUDGING
        return selfTestSubmissionRepository.save(submission)
    }
}
