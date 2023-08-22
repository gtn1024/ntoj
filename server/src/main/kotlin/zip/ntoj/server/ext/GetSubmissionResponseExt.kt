package zip.ntoj.server.ext

import zip.ntoj.server.model.FileUpload
import zip.ntoj.server.model.Language
import zip.ntoj.shared.model.GetSubmissionResponse
import zip.ntoj.shared.model.TestcaseDto

fun TestcaseDto.Companion.from(fileUpload: FileUpload) = TestcaseDto(
    fileId = fileUpload.fileId!!,
    hash = fileUpload.hash,
)

fun GetSubmissionResponse.LanguageDto.Companion.from(language: Language) = GetSubmissionResponse.LanguageDto(
    languageId = language.languageId!!,
    name = language.languageName,
    compileCommand = language.compileCommand,
    executeCommand = language.executeCommand,
    type = language.type,
    timeLimitRate = language.timeLimitRate,
    memoryLimitRate = language.memoryLimitRate,
)
