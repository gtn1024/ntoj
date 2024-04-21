package com.github.ntoj.app.server.controller

import com.github.ntoj.app.server.ext.success
import com.github.ntoj.app.server.model.L
import com.github.ntoj.app.server.model.entities.Language
import com.github.ntoj.app.server.service.LanguageService
import com.github.ntoj.app.shared.model.R
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/language")
class LanguageController(
    private val languageService: LanguageService,
) {
    @GetMapping
    fun index(): ResponseEntity<R<L<LanguageDto>>> {
        val languages =
            languageService.get()
                .filter { it.enabled }
        return R.success(
            200,
            "获取成功",
            L(languages.size * 1L, 1, languages.map { LanguageDto.from(it) }),
        )
    }

    data class LanguageDto(
        val id: Long,
        val name: String,
    ) {
        companion object {
            fun from(language: Language): LanguageDto =
                LanguageDto(
                    id = language.languageId!!,
                    name = language.languageName,
                )
        }
    }
}
