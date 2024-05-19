package com.github.ntoj.app.shared.model

data class JudgerRecordDto(
    val recordId: String,
    val problemId: Long?,
    val code: String,
    val lang: LanguageStructure,
    val testcase: TestcaseDto?,
    val timeLimit: Int,
    val memoryLimit: Int,
    val origin: RecordOrigin,
    val input: String?,
)

enum class RecordOrigin {
    PROBLEM,
    CONTEST,
    SELF_TEST,
}

data class TestcaseDto(
    val fileId: Long,
    val hash: String,
) {
    companion object
}
