package zip.ntoj.server.service

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import zip.ntoj.server.exception.AppException
import zip.ntoj.server.model.JudgeClientToken
import zip.ntoj.server.repository.JudgeClientTokenRepository

interface JudgeClientTokenService {
    fun get(id: Long): JudgeClientToken
    fun get(token: String): JudgeClientToken
    fun exists(token: String): Boolean
    fun get(
        page: Int = 1,
        pageSize: Int = Int.MAX_VALUE,
    ): List<JudgeClientToken>

    fun new(judgeClientToken: JudgeClientToken): JudgeClientToken
    fun update(judgeClientToken: JudgeClientToken): JudgeClientToken
    fun count(): Long
    fun delete(id: Long)
}

@Service
class JudgeClientTokenServiceImpl(
    private val judgeClientTokenRepository: JudgeClientTokenRepository,
) : JudgeClientTokenService {
    override fun get(id: Long): JudgeClientToken {
        return judgeClientTokenRepository.findById(id).orElseThrow { AppException("Token 不存在", 404) }
    }

    override fun get(token: String): JudgeClientToken {
        return judgeClientTokenRepository.findByToken(token).orElseThrow { AppException("Token 不存在", 404) }
    }

    override fun get(page: Int, pageSize: Int): List<JudgeClientToken> {
        return judgeClientTokenRepository.findAll(PageRequest.of(page - 1, pageSize)).toList()
    }

    override fun exists(token: String): Boolean {
        return judgeClientTokenRepository.existsByToken(token)
    }

    override fun new(judgeClientToken: JudgeClientToken): JudgeClientToken {
        return judgeClientTokenRepository.save(judgeClientToken)
    }

    override fun update(judgeClientToken: JudgeClientToken): JudgeClientToken {
        return judgeClientTokenRepository.save(judgeClientToken)
    }

    override fun count(): Long {
        return judgeClientTokenRepository.count()
    }

    override fun delete(id: Long) {
        judgeClientTokenRepository.deleteById(id)
    }
}
