package zip.ntoj.server.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import zip.ntoj.server.model.L
import zip.ntoj.server.model.Language
import zip.ntoj.server.model.R
import zip.ntoj.server.service.LanguageService

@RestController
@RequestMapping("/language")
class LanguageController(
    private val languageService: LanguageService,
) {
    @GetMapping
    fun index(): ResponseEntity<R<L<LanguageDto>>> {
        val languages = languageService.get()
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
            fun from(language: Language): LanguageDto = LanguageDto(
                id = language.languageId!!,
                name = language.languageName,
            )
        }
    }
}
