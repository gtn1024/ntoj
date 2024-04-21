package com.github.ntoj.app.server.ext

import com.github.ntoj.app.server.model.entities.FileUpload
import com.github.ntoj.app.server.model.entities.Language
import com.github.ntoj.app.shared.model.LanguageDto
import com.github.ntoj.app.shared.model.TestcaseDto

fun TestcaseDto.Companion.from(fileUpload: FileUpload) =
    TestcaseDto(
        fileId = fileUpload.fileId!!,
        hash = fileUpload.hash,
    )

fun LanguageDto.Companion.from(language: Language) =
    LanguageDto(
        languageId = language.languageId!!,
        name = language.languageName,
        compileCommand = language.compileCommand,
        executeCommand = language.executeCommand,
        timeLimitRate = language.timeLimitRate,
        memoryLimitRate = language.memoryLimitRate,
        sourceFilename = language.sourceFilename,
        targetFilename = language.targetFilename,
    )
