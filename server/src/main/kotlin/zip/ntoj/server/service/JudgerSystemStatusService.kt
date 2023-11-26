package zip.ntoj.server.service

import org.springframework.stereotype.Service
import zip.ntoj.server.model.JudgerSystemStatus
import zip.ntoj.server.repository.JudgerSystemStatusRepository

interface JudgerSystemStatusService {
    fun findByJudgerId(id: String): JudgerSystemStatus?
    fun new(judgerSystemStatus: JudgerSystemStatus)
    fun update(judgerSystemStatus: JudgerSystemStatus)
}

@Service
class JudgerSystemStatusServiceImpl(
    private val judgerSystemStatusRepository: JudgerSystemStatusRepository,
) : JudgerSystemStatusService {
    override fun findByJudgerId(id: String): JudgerSystemStatus? {
        return judgerSystemStatusRepository.findByJudgerId(id).orElse(null)
    }

    override fun new(judgerSystemStatus: JudgerSystemStatus) {
        judgerSystemStatusRepository.save(judgerSystemStatus)
    }

    override fun update(judgerSystemStatus: JudgerSystemStatus) {
        judgerSystemStatusRepository.save(judgerSystemStatus)
    }
}
