package zip.ntoj.shared.model

data class UpdateSelfTestSubmissionRequest(
    val submissionId: Long,
    val result: SubmissionStatus,
    val time: Int?,
    val memory: Int?,

    val compileLog: String? = null,

    val judgeStage: JudgeStage,
    val output: String? = null,
)
