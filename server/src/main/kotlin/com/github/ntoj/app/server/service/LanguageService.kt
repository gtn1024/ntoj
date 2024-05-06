package com.github.ntoj.app.server.service

import com.github.ntoj.app.shared.model.LanguageStructure
import org.springframework.stereotype.Service

interface LanguageService {
    fun exists(id: String): Boolean
}

@Service
class LanguageServiceImpl(
    private val languages: Map<String, LanguageStructure>,
) : LanguageService {
    override fun exists(id: String): Boolean {
        return languages.containsKey(id)
    }
}
