package zip.ntoj.server.controller.admin

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.annotation.SaMode
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import zip.ntoj.server.model.L
import zip.ntoj.server.model.Language
import zip.ntoj.server.model.R
import zip.ntoj.server.service.LanguageService

@RestController
@RequestMapping("/admin/language")
@SaCheckLogin
@SaCheckRole(value = ["SUPER_ADMIN"], mode = SaMode.OR)
class AdminLanguageController(
    private val languageService: LanguageService,
) {
    @GetMapping
    fun index(
        @RequestParam(required = false, defaultValue = "1") current: Int,
        @RequestParam(required = false, defaultValue = "20") pageSize: Int,
    ): ResponseEntity<R<L<LanguageDto>>> {
        val list = languageService.get(page = current, pageSize = pageSize)
        val count = languageService.count()
        return R.success(
            200,
            "获取成功",
            L(
                total = count,
                page = current,
                list = list.map { LanguageDto.from(it) },
            ),
        )
    }

    @GetMapping("{id}")
    fun get(@PathVariable id: Long): ResponseEntity<R<LanguageDto>> {
        val language = languageService.get(id)
        return R.success(
            200,
            "获取成功",
            LanguageDto.from(language),
        )
    }

    @PostMapping
    fun create(
        @RequestBody @Valid
        languageRequest: LanguageRequest,
    ): ResponseEntity<R<LanguageDto>> {
        return R.success(
            200,
            "创建成功",
            LanguageDto.from(
                languageService.new(
                    Language(
                        languageName = languageRequest.languageName,
                        compileCommand = languageRequest.compileCommand,
                        executeCommand = languageRequest.executeCommand,
                        type = Language.LanguageType.valueOf(languageRequest.type),
                        enabled = languageRequest.enabled,
                    ),
                ),
            ),
        )
    }

    @PatchMapping("{id}")
    fun update(
        @RequestBody @Valid
        languageRequest: LanguageRequest,
        @PathVariable id: Long,
    ): ResponseEntity<R<LanguageDto>> {
        val language = languageService.get(id)
        if (language.languageName != languageRequest.languageName) {
            language.languageName = languageRequest.languageName
        }
        if (language.compileCommand != languageRequest.compileCommand) {
            language.compileCommand = languageRequest.compileCommand
        }
        if (language.executeCommand != languageRequest.executeCommand) {
            language.executeCommand = languageRequest.executeCommand
        }
        if (language.type != Language.LanguageType.valueOf(languageRequest.type)) {
            language.type = Language.LanguageType.valueOf(languageRequest.type)
        }
        if (language.enabled != languageRequest.enabled) {
            language.enabled = languageRequest.enabled
        }
        return R.success(
            200,
            "修改成功",
            LanguageDto.from(languageService.update(language)),
        )
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<R<Unit>> {
        languageService.delete(id)
        return R.success(200, "删除成功")
    }
}

data class LanguageDto(
    var languageName: String,
    var compileCommand: String?,
    var executeCommand: String?,
    var type: Language.LanguageType,
    var enabled: Boolean,
    var id: Long,
) {
    companion object {
        fun from(language: Language): LanguageDto {
            return LanguageDto(
                languageName = language.languageName,
                compileCommand = language.compileCommand,
                executeCommand = language.executeCommand,
                type = language.type,
                enabled = language.enabled,
                id = language.languageId!!,
            )
        }
    }
}

data class LanguageRequest(
    var languageName: String,
    var compileCommand: String?,
    var executeCommand: String?,
    var type: String,
    var enabled: Boolean,
)
