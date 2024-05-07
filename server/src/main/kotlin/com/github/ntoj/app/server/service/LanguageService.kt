package com.github.ntoj.app.server.service

import com.github.ntoj.app.server.config.system.LanguageMap
import org.springframework.stereotype.Service

interface LanguageService {
    fun exists(id: String): Boolean
}

@Service
class LanguageServiceImpl(
    private val languages: LanguageMap,
) : LanguageService {
    override fun exists(id: String): Boolean {
        return languages.containsKey(id)
    }
}
