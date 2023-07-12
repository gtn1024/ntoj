package zip.ntoj.server.service

import org.springframework.stereotype.Service
import zip.ntoj.server.exception.TojException
import zip.ntoj.server.model.Submission
import zip.ntoj.server.repository.SubmissionRepository

interface SubmissionService {
    fun get(id: Long): Submission
    fun new(submission: Submission): Submission
    fun update(submission: Submission): Submission
}

@Service
class SubmissionServiceImpl(
    val submissionRepository: SubmissionRepository,
) : SubmissionService {
    override fun get(id: Long): Submission {
        return submissionRepository.findById(id).orElseThrow { TojException("提交不存在", 404) }
    }

    override fun new(submission: Submission): Submission {
        return submissionRepository.save(submission)
    }

    override fun update(submission: Submission): Submission {
        return submissionRepository.save(submission)
    }
}
