package zip.ntoj.judger

import zip.ntoj.shared.model.GetSubmissionResponse
import zip.ntoj.shared.model.SubmissionStatus
import zip.ntoj.shared.model.TestcaseJudgeResult
import zip.ntoj.shared.util.removeLastEmptyLine
import zip.ntoj.shared.util.trimByLine
import java.io.File

object TestcaseRunner {
    suspend fun runTestcase(
        targetName: String,
        submission: GetSubmissionResponse,
        fileId: String,
    ): JudgeResult {
        val number = getTestcaseNumber(submission.testcase.fileId)
        var maxTime = 0L
        var maxMemory = 0L
        val testcaseJudgeResults = mutableListOf<TestcaseJudgeResult>()
        var judgeResult = SubmissionStatus.ACCEPTED
        for (i in 1..number) {
            runSingleTestcase(submission, targetName, fileId, i).let {
                testcaseJudgeResults.add(it)
                maxTime = maxTime.coerceAtLeast(it.time)
                maxMemory = maxMemory.coerceAtLeast(it.memory)
                if (it.status != SubmissionStatus.ACCEPTED && judgeResult == SubmissionStatus.ACCEPTED) {
                    judgeResult = it.status
                }
            }
        }
        return JudgeResult(testcaseJudgeResults, judgeResult, maxTime, maxMemory)
    }

    private suspend fun runSingleTestcase(
        submission: GetSubmissionResponse,
        targetName: String,
        fileId: String,
        idx: Int,
    ): TestcaseJudgeResult {
        val inData = File("testcase/${submission.testcase.fileId}/$idx.in").readText()
        val body = getRunBody(submission, targetName, inData, fileId)
        val result = Client.Sandbox.run(body)
        if (result.size != 1) {
            println("result.size != 1")
            return TestcaseJudgeResult(SubmissionStatus.SYSTEM_ERROR, 0, 0)
        }
        val res = result[0]
        if (res.status != SandboxStatus.Accepted) {
            val st = when (res.status) {
                SandboxStatus.MemoryLimitExceeded -> SubmissionStatus.MEMORY_LIMIT_EXCEEDED
                SandboxStatus.TimeLimitExceeded -> SubmissionStatus.TIME_LIMIT_EXCEEDED
                SandboxStatus.OutputLimitExceeded -> SubmissionStatus.OUTPUT_LIMIT_EXCEEDED
                SandboxStatus.InternalError -> SubmissionStatus.SYSTEM_ERROR
                else -> SubmissionStatus.RUNTIME_ERROR
            }
            return TestcaseJudgeResult(st, 0, 0)
        }
        val stdout = File("testcase/${submission.testcase.fileId}/$idx.out").readText()
            .trimByLine()
            .removeLastEmptyLine()

        if (res.files["stdout"]?.trimByLine()?.removeLastEmptyLine() != stdout) {
            return TestcaseJudgeResult(SubmissionStatus.WRONG_ANSWER, 0, 0)
        }
        return TestcaseJudgeResult(SubmissionStatus.ACCEPTED, res.time / 1000 / 1000, res.memory / 1024)
    }

    private fun getRunBody(
        submission: GetSubmissionResponse,
        targetName: String,
        inData: String,
        fileId: String,
    ): SandboxRequest {
        val executeCommand = submission.language.executeCommand!!
            .replace("{target}", targetName)
        return SandboxRequest(
            cmd = listOf(
                Cmd(
                    args = listOf("/usr/bin/bash", "-c", executeCommand),
                    env = listOf("PATH=/usr/bin:/bin"),
                    files = listOf(
                        MemoryFile(inData),
                        Collector(name = "stdout", max = 10240),
                        Collector(name = "stderr", max = 10240),
                    ),
                    cpuLimit = 1L * submission.timeLimit * 1000 * 1000,
                    clockLimit = 1L * submission.timeLimit * 1000 * 1000 * 2,
                    memoryLimit = 1L * submission.memoryLimit * 1024 * 1024,
                    stackLimit = 1L * submission.memoryLimit * 1024 * 1024,
                    procLimit = 50,
                    copyIn = mapOf(
                        targetName to PreparedFile(fileId),
                    ),
                ),
            ),
        )
    }
}
