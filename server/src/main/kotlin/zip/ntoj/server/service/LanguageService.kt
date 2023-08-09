package zip.ntoj.server.service

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import zip.ntoj.server.exception.AppException
import zip.ntoj.server.model.Language
import zip.ntoj.server.repository.LanguageRepository

interface LanguageService {
    fun get(id: Long): Language
    fun get(
        page: Int = 1,
        pageSize: Int = Int.MAX_VALUE,
    ): List<Language>

    fun new(problem: Language): Language
    fun update(problem: Language): Language

    fun count(): Long

    fun delete(id: Long)
}

@Service
class LanguageServiceImpl(
    private val languageRepository: LanguageRepository,
) : LanguageService {
    override fun get(id: Long): Language {
        return languageRepository.findById(id).orElseThrow { AppException("语言不存在", 404) }
    }

    override fun get(page: Int, pageSize: Int): List<Language> {
        return languageRepository.findAll(PageRequest.of(page - 1, pageSize)).toList()
    }

    override fun new(problem: Language): Language {
        return languageRepository.save(problem)
    }

    override fun update(problem: Language): Language {
        return languageRepository.save(problem)
    }

    override fun count(): Long {
        return languageRepository.count()
    }

    override fun delete(id: Long) {
        languageRepository.deleteById(id)
    }
}
