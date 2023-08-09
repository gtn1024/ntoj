package zip.ntoj.shared.dtos.judge

data class GetSubmissionResponse(
    val submissionId: Long,
    val problemId: Long,
    val code: String,
    val language: String,
)

data class SubmissionJudgeResult(
    val submissionId: Long,
    val problemId: Long,
    val code: String,
    val language: String,
    val result: SubmissionStatus,
    val time: Int,
    val memory: Int,

    val judgerId: String? = null,
)

enum class SubmissionStatus {
    PENDING, // 0
    JUDGING, // 1
    ACCEPTED, // 2
    WRONG_ANSWER, // 3
    TIME_LIMIT_EXCEEDED, // 4
    MEMORY_LIMIT_EXCEEDED, // 5
    RUNTIME_ERROR, // 6
    COMPILE_ERROR, // 7
    SYSTEM_ERROR, // 8
    PRESENTATION_ERROR, // 9
    DEPRECATED, // 10
}
