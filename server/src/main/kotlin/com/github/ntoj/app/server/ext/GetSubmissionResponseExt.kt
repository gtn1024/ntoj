package com.github.ntoj.app.server.ext

import com.github.ntoj.app.server.model.entities.FileUpload
import com.github.ntoj.app.shared.model.TestcaseDto

fun TestcaseDto.Companion.from(fileUpload: FileUpload) =
    TestcaseDto(
        fileId = fileUpload.fileId!!,
        hash = fileUpload.hash,
    )
