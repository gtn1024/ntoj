package zip.ntoj.server.ext

import zip.ntoj.server.model.FileUpload
import zip.ntoj.server.model.Language
import zip.ntoj.shared.model.LanguageDto
import zip.ntoj.shared.model.TestcaseDto

fun TestcaseDto.Companion.from(fileUpload: FileUpload) = TestcaseDto(
    fileId = fileUpload.fileId!!,
    hash = fileUpload.hash,
)

fun LanguageDto.Companion.from(language: Language) = LanguageDto(
    languageId = language.languageId!!,
    name = language.languageName,
    compileCommand = language.compileCommand,
    executeCommand = language.executeCommand,
    timeLimitRate = language.timeLimitRate,
    memoryLimitRate = language.memoryLimitRate,
    sourceFilename = language.sourceFilename,
    targetFilename = language.targetFilename,
)
