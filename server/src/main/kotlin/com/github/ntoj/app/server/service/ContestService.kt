package com.github.ntoj.app.server.service

import jakarta.persistence.criteria.Predicate
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import com.github.ntoj.app.server.exception.AppException
import com.github.ntoj.app.server.model.Contest
import com.github.ntoj.app.server.repository.ContestRepository

interface ContestService {
    fun get(id: Long): Contest

    fun get(
        onlyVisible: Boolean = false,
        page: Int = 1,
        pageSize: Int = Int.MAX_VALUE,
        desc: Boolean = false,
    ): List<Contest>

    fun count(onlyVisible: Boolean = false): Long

    fun add(contest: Contest): Contest

    fun update(contest: Contest): Contest

    fun delete(id: Long)

    fun exists(id: Long): Boolean
}

@Service
class ContestServiceImpl(
    private val contestRepository: ContestRepository,
) : ContestService {
    override fun get(id: Long): Contest {
        return contestRepository.findById(id).orElseThrow { AppException("竞赛不存在", 404) }
    }

    override fun get(
        onlyVisible: Boolean,
        page: Int,
        pageSize: Int,
        desc: Boolean,
    ): List<Contest> {
        return contestRepository.findAll(
            buildSpecification(onlyVisible),
            PageRequest.of(
                page - 1,
                pageSize,
                Sort.by(if (desc) Sort.Direction.DESC else Sort.Direction.ASC, "contestId"),
            ),
        ).toList()
    }

    override fun count(onlyVisible: Boolean): Long {
        return contestRepository.count(buildSpecification(onlyVisible))
    }

    override fun add(contest: Contest): Contest {
        if (contest.contestId != null) throw AppException("竞赛ID不应该存在", 400)
        return contestRepository.save(contest)
    }

    override fun update(contest: Contest): Contest {
        if (contest.contestId == null) throw AppException("竞赛ID不应该不存在", 400)
        return contestRepository.save(contest)
    }

    override fun delete(id: Long) {
        contestRepository.deleteById(id)
    }

    override fun exists(id: Long): Boolean {
        return contestRepository.existsById(id)
    }

    private fun buildSpecification(onlyVisible: Boolean): Specification<Contest> {
        return Specification { root, _, criteriaBuilder ->
            val predicateList = mutableListOf<Predicate>()
            if (onlyVisible) {
                predicateList.add(criteriaBuilder.isTrue(root.get("visible")))
            }
            criteriaBuilder.and(*predicateList.toTypedArray())
        }
    }
}
