package com.github.ntoj.app.shared.model

data class UpdateRecordRequest(
    val time: Int?,
    val memory: Int?,
    val compileLog: String? = null,
    val judgerId: String? = null,
    val testcaseResult: List<TestcaseJudgeResult> = listOf(),
)
