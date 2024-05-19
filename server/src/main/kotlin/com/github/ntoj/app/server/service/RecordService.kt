package com.github.ntoj.app.server.service

import com.github.ntoj.app.server.exception.AppException
import com.github.ntoj.app.server.model.entities.Contest
import com.github.ntoj.app.server.model.entities.Problem
import com.github.ntoj.app.server.model.entities.Record
import com.github.ntoj.app.server.model.entities.User
import com.github.ntoj.app.server.repository.RecordRepository
import com.github.ntoj.app.shared.model.JudgeStage
import com.github.ntoj.app.shared.model.RecordOrigin
import com.github.ntoj.app.shared.model.SubmissionStatus
import jakarta.persistence.criteria.Join
import jakarta.persistence.criteria.Predicate
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Instant

interface RecordService {
    fun list(
        page: Int = 1,
        pageSize: Int = 50,
        desc: Boolean = false,
        allRecord: Boolean = false,
    ): List<Record>

    fun count(allRecord: Boolean = false): Long

    fun create(record: Record)

    fun update(record: Record)

    fun get(id: String): Record?

    fun getPendingRecords(
        limit: Int,
        from: String = "0",
    ): List<Record>

    fun setJudging(recordId: String)

    fun getByContest(
        contestId: Long,
        page: Int = 1,
        pageSize: Int = Int.MAX_VALUE,
        desc: Boolean = false,
        username: String? = null,
    ): List<Record>

    fun countByContest(
        contestId: Long,
        username: String? = null,
    ): Long

    fun rejudge(id: String)

    fun getSolvedHomeworkProblemRecord(
        user: User,
        problem: Problem,
        endTime: Instant,
    ): Record?
}

@Service
class RecordServiceImpl(
    val recordRepository: RecordRepository,
) : RecordService {
    override fun list(
        page: Int,
        pageSize: Int,
        desc: Boolean,
        allRecord: Boolean,
    ): List<Record> {
        val spec =
            Specification<Record> { root, query, cb ->
                val predicates: MutableList<Predicate> = mutableListOf()
                if (!allRecord) {
                    predicates.add(cb.equal(root.get<String>("origin"), "PROBLEM"))
                }
                return@Specification cb.and(*predicates.toTypedArray())
            }
        val pageRequest =
            PageRequest.of(
                page - 1,
                pageSize,
                Sort.by(if (desc) Sort.Direction.DESC else Sort.Direction.ASC, "recordId"),
            )
        return recordRepository.findAll(spec, pageRequest).toList()
    }

    override fun count(allRecord: Boolean): Long {
        val spec =
            Specification<Record> { root, query, cb ->
                val predicates: MutableList<Predicate> = mutableListOf()
                if (!allRecord) {
                    predicates.add(cb.equal(root.get<String>("origin"), "PROBLEM"))
                }
                return@Specification cb.and(*predicates.toTypedArray())
            }
        return recordRepository.count(spec)
    }

    override fun create(record: Record) {
        recordRepository.save(record)
    }

    override fun update(record: Record) {
        recordRepository.save(record)
    }

    override fun get(id: String): Record? {
        return recordRepository.findByIdOrNull(id)
    }

    override fun getPendingRecords(
        limit: Int,
        from: String,
    ): List<Record> {
        val spec =
            Specification<Record> { root, query, cb ->
                val predicates: MutableList<Predicate> = mutableListOf()
                predicates.add(cb.equal(root.get<JudgeStage>("stage"), JudgeStage.PENDING))
                predicates.add(cb.greaterThanOrEqualTo(root.get("recordId"), from))
                return@Specification cb.and(*predicates.toTypedArray())
            }
        val pageRequest =
            PageRequest.of(
                0,
                limit,
                Sort.by(Sort.Direction.ASC, "recordId"),
            )
        return recordRepository.findAll(spec, pageRequest).toList()
    }

    override fun setJudging(recordId: String) {
        val record = recordRepository.findById(recordId).orElseThrow { AppException("提交不存在", 404) }
        record.stage = JudgeStage.JUDGING
        recordRepository.save(record)
    }

    override fun getByContest(
        contestId: Long,
        page: Int,
        pageSize: Int,
        desc: Boolean,
        username: String?,
    ): List<Record> {
        val spec =
            Specification<Record> { root, _, cb ->
                val contestJoin: Join<Record, Contest> = root.join("contest")
                val predicates: MutableList<Predicate> = mutableListOf()
                predicates.add(cb.equal(contestJoin.get<Long>("contestId"), contestId))
                if (username != null) {
                    predicates.add(cb.equal(root.get<User>("user").get<String>("username"), username))
                }
                return@Specification cb.and(*predicates.toTypedArray())
            }
        val pageRequest =
            PageRequest.of(
                page - 1,
                pageSize,
                Sort.by(if (desc) Sort.Direction.DESC else Sort.Direction.ASC, "recordId"),
            )
        return recordRepository.findAll(spec, pageRequest).toList()
    }

    override fun countByContest(
        contestId: Long,
        username: String?,
    ): Long {
        val spec =
            Specification<Record> { root, _, cb ->
                val contestJoin: Join<Record, Contest> = root.join("contest")
                val predicates: MutableList<Predicate> = mutableListOf()
                predicates.add(cb.equal(contestJoin.get<Long>("contestId"), contestId))
                if (username != null) {
                    predicates.add(cb.equal(root.get<User>("user").get<String>("username"), username))
                }
                return@Specification cb.and(*predicates.toTypedArray())
            }
        return recordRepository.count(spec)
    }

    override fun rejudge(id: String) {
        val record = recordRepository.findById(id).orElseThrow { AppException("提交不存在", 404) }
        record.stage = JudgeStage.PENDING
        record.status = SubmissionStatus.PENDING
        recordRepository.save(record)
    }

    override fun getSolvedHomeworkProblemRecord(
        user: User,
        problem: Problem,
        endTime: Instant,
    ): Record? {
        val spec =
            Specification<Record> { root, query, cb ->
                val predicates: MutableList<Predicate> = mutableListOf()
                predicates.add(cb.equal(root.get<User>("user"), user))
                predicates.add(cb.equal(root.get<Problem>("problem"), problem))
                predicates.add(cb.equal(root.get<SubmissionStatus>("status"), SubmissionStatus.ACCEPTED))
                predicates.add(
                    cb.equal(
                        root.get<RecordOrigin>("origin"),
                        RecordOrigin.PROBLEM,
                    ),
                )
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), endTime))
                query.orderBy(cb.desc(root.get<Long>("recordId")))
                return@Specification cb.and(*predicates.toTypedArray())
            }
        return recordRepository.findAll(spec).firstOrNull()
    }
}
