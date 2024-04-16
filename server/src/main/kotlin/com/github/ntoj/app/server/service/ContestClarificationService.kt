package com.github.ntoj.app.server.service

import com.github.ntoj.app.server.exception.AppException
import com.github.ntoj.app.server.model.ContestClarification
import com.github.ntoj.app.server.repository.ContestClarificationRepository
import org.springframework.stereotype.Service

interface ContestClarificationService {
    fun get(id: Long): ContestClarification

    fun get(
        onlyVisible: Boolean = false,
        page: Int = 1,
        pageSize: Int = Int.MAX_VALUE,
        desc: Boolean = false,
    ): List<ContestClarification>

    fun count(onlyVisible: Boolean = false): Long

    fun add(clarification: ContestClarification): ContestClarification

    fun update(clarification: ContestClarification): ContestClarification

    fun delete(id: Long)

    fun exists(id: Long): Boolean

    fun getByContestId(contestId: Long): List<ContestClarification>
}

@Service
class ContestClarificationServiceImpl(
    private val contestClarificationRepository: ContestClarificationRepository,
) : ContestClarificationService {
    override fun get(id: Long): ContestClarification {
        return contestClarificationRepository.findById(id).orElseThrow { AppException("不存在", 404) }
    }

    override fun get(
        onlyVisible: Boolean,
        page: Int,
        pageSize: Int,
        desc: Boolean,
    ): List<ContestClarification> {
        TODO("Not yet implemented")
    }

    override fun count(onlyVisible: Boolean): Long {
        TODO("Not yet implemented")
    }

    override fun add(clarification: ContestClarification): ContestClarification {
        return contestClarificationRepository.save(clarification)
    }

    override fun update(clarification: ContestClarification): ContestClarification {
        if (!contestClarificationRepository.existsById(clarification.clarificationId!!)) {
            throw AppException("不存在", 404)
        }
        return contestClarificationRepository.save(clarification)
    }

    override fun delete(id: Long) {
        TODO("Not yet implemented")
    }

    override fun exists(id: Long): Boolean {
        TODO("Not yet implemented")
    }

    override fun getByContestId(contestId: Long): List<ContestClarification> {
        return contestClarificationRepository.findAllByContestContestId(contestId)
    }
}
