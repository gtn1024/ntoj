package zip.ntoj.shared.model

data class UpdateSubmissionRequest(
    val submissionId: Long,
    val problemId: Long,
    val result: SubmissionStatus,
    val time: Int?,
    val memory: Int?,

    val judgerId: String? = null,
    val judgeStage: JudgeStage,
)
