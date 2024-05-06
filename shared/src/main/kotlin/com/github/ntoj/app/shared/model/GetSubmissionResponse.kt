package com.github.ntoj.app.shared.model

data class GetSubmissionResponse(
    val submissionId: Long,
    val problemId: Long,
    val code: String,
    val lang: LanguageStructure,
    val testcase: TestcaseDto,
    val timeLimit: Int,
    val memoryLimit: Int,
)

data class GetSelfTestSubmissionResponse(
    val submissionId: Long,
    val code: String,
    val lang: LanguageStructure,
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
