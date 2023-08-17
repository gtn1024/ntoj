package zip.ntoj.shared.dtos.judge

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
    OUTPUT_LIMIT_EXCEEDED, // 10
    DEPRECATED, // 11
}
