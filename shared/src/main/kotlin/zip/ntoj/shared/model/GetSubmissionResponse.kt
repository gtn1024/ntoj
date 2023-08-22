package zip.ntoj.shared.model

data class GetSubmissionResponse(
    val submissionId: Long,
    val problemId: Long,
    val code: String,
    val language: LanguageDto,
    val testcase: TestcaseDto,
    val timeLimit: Int,
    val memoryLimit: Int,
) {
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
}

data class TestcaseDto(
    val fileId: Long,
    val hash: String,
) {
    companion object
}
