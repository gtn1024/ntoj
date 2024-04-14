package com.github.ntoj.app.shared.model

data class UpdateSubmissionRequest(
    val submissionId: Long,
    val result: SubmissionStatus,
    val time: Int?,
    val memory: Int?,
    val compileLog: String? = null,
    val judgerId: String? = null,
    val judgeStage: JudgeStage,
    val testcaseResult: List<TestcaseJudgeResult> = listOf(),
)
