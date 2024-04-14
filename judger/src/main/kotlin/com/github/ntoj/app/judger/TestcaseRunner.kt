package com.github.ntoj.app.judger

import com.github.ntoj.app.shared.model.GetSelfTestSubmissionResponse
import com.github.ntoj.app.shared.model.GetSubmissionResponse
import com.github.ntoj.app.shared.model.LanguageDto
import com.github.ntoj.app.shared.model.SubmissionStatus
import com.github.ntoj.app.shared.model.TestcaseJudgeResult
import com.github.ntoj.app.shared.util.ZipUtils
import com.github.ntoj.app.shared.util.removeLastEmptyLine
import com.github.ntoj.app.shared.util.trimByLine
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

    suspend fun runSelfTest(
        targetName: String,
        submission: GetSelfTestSubmissionResponse,
        fileId: String,
        inData: String,
        expectedOutput: String?,
    ): SelfTestJudgeResult {
        val body =
            getRunBody(submission.language, submission.timeLimit, submission.memoryLimit, targetName, inData, fileId)
        val result = Client.Sandbox.run(body)
        if (result.size != 1) {
            return SelfTestJudgeResult(SubmissionStatus.SYSTEM_ERROR, 0, 0, null)
        }
        val res = result[0]
        if (res.status != SandboxStatus.Accepted) {
            val st =
                when (res.status) {
                    SandboxStatus.MemoryLimitExceeded -> SubmissionStatus.MEMORY_LIMIT_EXCEEDED
                    SandboxStatus.TimeLimitExceeded -> SubmissionStatus.TIME_LIMIT_EXCEEDED
                    SandboxStatus.OutputLimitExceeded -> SubmissionStatus.OUTPUT_LIMIT_EXCEEDED
                    SandboxStatus.InternalError -> SubmissionStatus.SYSTEM_ERROR
                    else -> SubmissionStatus.RUNTIME_ERROR
                }
            return SelfTestJudgeResult(st, res.time / 1000 / 1000, res.memory / 1024, null)
        }
        val expectedStdout = expectedOutput?.trimByLine()?.removeLastEmptyLine()
        val stdout = res.files["stdout"]?.trimByLine()?.removeLastEmptyLine()
        if (expectedStdout == null) {
            return SelfTestJudgeResult(SubmissionStatus.ACCEPTED, res.time / 1000 / 1000, res.memory / 1024, stdout)
        }
        if (stdout != expectedStdout) {
            return SelfTestJudgeResult(SubmissionStatus.WRONG_ANSWER, res.time / 1000 / 1000, res.memory / 1024, stdout)
        }
        return SelfTestJudgeResult(SubmissionStatus.ACCEPTED, res.time / 1000 / 1000, res.memory / 1024, stdout)
    }

    private fun getTestcaseNumber(testcase: Long): Int {
        return ZipUtils.getFilenamesFromZip(File("testcase/$testcase.zip")).size / 2
    }

    private suspend fun runSingleTestcase(
        submission: GetSubmissionResponse,
        targetName: String,
        fileId: String,
        idx: Int,
    ): TestcaseJudgeResult {
        val inData = File("testcase/${submission.testcase.fileId}/$idx.in").readText()
        val body =
            getRunBody(submission.language, submission.timeLimit, submission.memoryLimit, targetName, inData, fileId)
        val result = Client.Sandbox.run(body)
        if (result.size != 1) {
            return TestcaseJudgeResult(SubmissionStatus.SYSTEM_ERROR, 0, 0)
        }
        val res = result[0]
        if (res.status != SandboxStatus.Accepted) {
            val st =
                when (res.status) {
                    SandboxStatus.MemoryLimitExceeded -> SubmissionStatus.MEMORY_LIMIT_EXCEEDED
                    SandboxStatus.TimeLimitExceeded -> SubmissionStatus.TIME_LIMIT_EXCEEDED
                    SandboxStatus.OutputLimitExceeded -> SubmissionStatus.OUTPUT_LIMIT_EXCEEDED
                    SandboxStatus.InternalError -> SubmissionStatus.SYSTEM_ERROR
                    else -> SubmissionStatus.RUNTIME_ERROR
                }
            return TestcaseJudgeResult(st, res.time / 1000 / 1000, res.memory / 1024)
        }
        val stdout =
            File("testcase/${submission.testcase.fileId}/$idx.out").readText()
                .trimByLine()
                .removeLastEmptyLine()

        if (res.files["stdout"]?.trimByLine()?.removeLastEmptyLine() != stdout) {
            return TestcaseJudgeResult(SubmissionStatus.WRONG_ANSWER, res.time / 1000 / 1000, res.memory / 1024)
        }
        return TestcaseJudgeResult(SubmissionStatus.ACCEPTED, res.time / 1000 / 1000, res.memory / 1024)
    }

    private fun getRunBody(
        language: LanguageDto,
        timeLimit: Int,
        memoryLimit: Int,
        targetName: String,
        inData: String,
        fileId: String,
    ): SandboxRequest {
        val executeCommand =
            language.executeCommand!!
                .replace("{target}", targetName)
        val memoryLimitRate = language.memoryLimitRate ?: 1
        val timeLimitRate = language.timeLimitRate ?: 1
        return SandboxRequest(
            cmd =
                listOf(
                    Cmd(
                        args = listOf("/usr/bin/bash", "-c", executeCommand),
                        env = listOf("PATH=/usr/bin:/bin"),
                        files =
                            listOf(
                                MemoryFile(inData),
                                // 50 KB
                                Collector(name = "stdout", max = 51_200),
                                // 50 KB
                                Collector(name = "stderr", max = 51_200),
                            ),
                        cpuLimit = 1L * timeLimit * 1000 * 1000 * timeLimitRate,
                        clockLimit = 1L * timeLimit * 1000 * 1000 * 2 * timeLimitRate,
                        memoryLimit = 1L * memoryLimit * 1024 * 1024 * memoryLimitRate,
                        stackLimit = 1L * memoryLimit * 1024 * 1024 * memoryLimitRate,
                        procLimit = 50,
                        copyIn =
                            mapOf(
                                targetName to PreparedFile(fileId),
                            ),
                        // 2 MB
                        copyOutMax = 1L * 2 * 1024 * 1024,
                    ),
                ),
        )
    }
}
