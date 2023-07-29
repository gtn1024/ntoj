package zip.ntoj.shared.dtos.judge

data class GetSubmissionResponse(
    val submissionId: Long,
    val problemId: Long,
    val code: String,
    val language: String,
)
