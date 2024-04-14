package com.github.ntoj.app.shared.model

data class GetSubmissionResponse(
    val submissionId: Long,
    val problemId: Long,
    val code: String,
    val language: LanguageDto,
    val testcase: TestcaseDto,
    val timeLimit: Int,
    val memoryLimit: Int,
)

data class LanguageDto(
    val languageId: Long,
    val name: String,
    val compileCommand: String?,
    val executeCommand: String?,
    val memoryLimitRate: Int?,
    val timeLimitRate: Int?,
    val sourceFilename: String?,
    val targetFilename: String?,
) {
    companion object
}

data class GetSelfTestSubmissionResponse(
    val submissionId: Long,
    val code: String,
    val language: LanguageDto,
    val timeLimit: Int,
    val memoryLimit: Int,
    val input: String,
    val expectedOutput: String?,
)

data class TestcaseDto(
    val fileId: Long,
    val hash: String,
) {
    companion object
}
