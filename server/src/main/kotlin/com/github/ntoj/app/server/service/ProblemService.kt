package com.github.ntoj.app.server.service

import com.github.ntoj.app.server.exception.AppException
import com.github.ntoj.app.server.model.Problem
import com.github.ntoj.app.server.repository.ProblemRepository
import jakarta.persistence.criteria.Predicate
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

interface ProblemService {
    fun get(id: Long): Problem

    fun get(alias: String): Problem

    fun get(
        onlyVisible: Boolean = false,
        page: Int = 1,
        pageSize: Int = Int.MAX_VALUE,
        desc: Boolean = false,
    ): List<Problem>

    fun count(onlyVisible: Boolean = false): Long

    fun new(problem: Problem): Problem

    fun update(problem: Problem): Problem

    fun delete(id: Long)

    fun exists(id: Long): Boolean
}

@Service
class ProblemServiceImpl(
    val problemRepository: ProblemRepository,
) : ProblemService {
    override fun get(id: Long): Problem {
        return problemRepository.findById(id).orElseThrow { AppException("题目不存在", 404) }
    }

    override fun get(alias: String): Problem {
        return problemRepository.findByAlias(alias).orElseThrow { AppException("题目不存在", 404) }
    }

    override fun get(
        onlyVisible: Boolean,
        page: Int,
        pageSize: Int,
        desc: Boolean,
    ): List<Problem> {
        return problemRepository.findAll(
            buildSpecification(onlyVisible),
            PageRequest.of(
                page - 1,
                pageSize,
                Sort.by(if (desc) Sort.Direction.DESC else Sort.Direction.ASC, "problemId"),
            ),
        ).toList()
    }

    override fun count(onlyVisible: Boolean): Long {
        return problemRepository.count(buildSpecification(onlyVisible))
    }

    override fun new(problem: Problem): Problem {
        if (!problemRepository.findByAlias(problem.alias).isEmpty) {
            throw AppException("alias已存在", 400)
        }
        return problemRepository.save(problem)
    }

    override fun update(problem: Problem): Problem {
        return problemRepository.save(problem)
    }

    override fun delete(id: Long) {
        problemRepository.deleteById(id)
    }

    override fun exists(id: Long): Boolean {
        return problemRepository.existsById(id)
    }

    private fun buildSpecification(onlyVisible: Boolean): Specification<Problem> {
        return Specification { root, _, criteriaBuilder ->
            val predicateList = mutableListOf<Predicate>()
            if (onlyVisible) {
                predicateList.add(criteriaBuilder.isTrue(root.get("visible")))
            }
            criteriaBuilder.and(*predicateList.toTypedArray())
        }
    }
}
