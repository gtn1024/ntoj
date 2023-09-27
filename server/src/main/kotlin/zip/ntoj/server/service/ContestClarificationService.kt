package zip.ntoj.server.service

import org.springframework.stereotype.Service
import zip.ntoj.server.model.ContestClarification
import zip.ntoj.server.repository.ContestClarificationRepository

interface ContestClarificationService {
    fun get(id: Long): ContestClarification
    fun get(
        onlyVisible: Boolean = false,
        page: Int = 1,
        pageSize: Int = Int.MAX_VALUE,
        desc: Boolean = false,
    ): List<ContestClarification>

    fun count(
        onlyVisible: Boolean = false,
    ): Long

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
        TODO("Not yet implemented")
    }

    override fun get(onlyVisible: Boolean, page: Int, pageSize: Int, desc: Boolean): List<ContestClarification> {
        TODO("Not yet implemented")
    }

    override fun count(onlyVisible: Boolean): Long {
        TODO("Not yet implemented")
    }

    override fun add(clarification: ContestClarification): ContestClarification {
        return contestClarificationRepository.save(clarification)
    }

    override fun update(clarification: ContestClarification): ContestClarification {
        TODO("Not yet implemented")
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
