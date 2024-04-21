package com.github.ntoj.app.server.service

import com.github.ntoj.app.server.exception.AppException
import com.github.ntoj.app.server.model.entities.Language
import com.github.ntoj.app.server.repository.LanguageRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

interface LanguageService {
    fun get(id: Long): Language

    fun get(
        page: Int = 1,
        pageSize: Int = Int.MAX_VALUE,
        desc: Boolean = false,
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

    override fun get(
        page: Int,
        pageSize: Int,
        desc: Boolean,
    ): List<Language> {
        return languageRepository.findAll(
            PageRequest.of(
                page - 1,
                pageSize,
                Sort.by(if (desc) Sort.Direction.DESC else Sort.Direction.ASC, "languageId"),
            ),
        ).toList()
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
