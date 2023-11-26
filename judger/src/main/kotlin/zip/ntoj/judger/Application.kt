package zip.ntoj.judger

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import zip.ntoj.shared.model.GetSelfTestSubmissionResponse
import zip.ntoj.shared.model.GetSubmissionResponse
import zip.ntoj.shared.model.JudgeStage
import zip.ntoj.shared.model.LanguageDto
import zip.ntoj.shared.model.SubmissionStatus
import zip.ntoj.shared.model.TestcaseJudgeResult
import zip.ntoj.shared.model.UpdateSelfTestSubmissionRequest
import zip.ntoj.shared.model.UpdateSubmissionRequest
import zip.ntoj.shared.util.fileMd5
import java.io.File
import java.net.ConnectException
import java.util.zip.ZipFile
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.deleteRecursively
import kotlin.io.path.exists
import kotlin.io.path.outputStream

private val LOGGER = LoggerFactory.getLogger("zip.ntoj.judger.Application")

fun showMessage() {
    LOGGER.info("NTOJ Judger")
    LOGGER.info("Server Host:    ${Configuration.SERVER_HOST}")
    LOGGER.info("Sandbox Server: ${Configuration.SANDBOX_SERVER}")
    LOGGER.info("Judger ID:      ${Configuration.JUDGER_ID}")
    LOGGER.info("Token:          ${Configuration.TOKEN}")
    LOGGER.info("Thread Count:   ${Configuration.THREAD_COUNT}")
    LOGGER.info("System info:")
    LOGGER.info("  OS:           ${Configuration.OS}")
    LOGGER.info("  Kernel:       ${Configuration.KERNEL}")
    LOGGER.info("  Memory Used:  ${Configuration.memoryUsed()}")
    LOGGER.info("  Memory Total: ${Configuration.memoryTotal()}")
}

suspend fun sandboxAvailable(): Boolean {
    return try {
        Client.Sandbox.version()
        true
    } catch (e: Exception) {
        false
    }
}

suspend fun run(id: Int) {
    var connected = false
    while (true) {
        try {
            if (!sandboxAvailable()) {
                LOGGER.error("(#$id) Sandbox server connect failed. Retry in 5s.")
                connected = false
                delay(5000)
                continue
            }
            val submission = Client.Backend.getSubmission()
            if (!connected) {
                LOGGER.info("(#$id) Connected! Waiting for submission.")
            }
            connected = true
            if (submission != null) {
                runSubmission(submission)
                continue
            }
            val selfTestSubmission = Client.Backend.getSelfTestSubmission()
            if (selfTestSubmission != null) {
                runSelfTestSubmission(selfTestSubmission)
                continue
            }
            // no content sleep 1 s
            delay(1000)
            continue
        } catch (e: IllegalStateException) {
            throw e
        } catch (e: ConnectException) {
            connected = false
            LOGGER.error("(#$id) Sandbox server connect failed. Retry in 5s.")
            delay(5000)
        } catch (e: Exception) {
            connected = false
            LOGGER.error("(#$id) Unknown error!", e)
        } finally {
            delay(1000)
        }
    }
}

fun main() {
    showMessage()
    runBlocking {
        repeat(Configuration.THREAD_COUNT) { id ->
            launch {
                run(id)
            }
        }
    }
}

private suspend fun runSelfTestSubmission(submission: GetSelfTestSubmissionResponse) {
    LOGGER.info("Received self test #${submission.submissionId}")
    setSelfTestSubmissionJudgeStage(submission.submissionId, JudgeStage.COMPILING)
    LOGGER.info("Now compiling #${submission.submissionId}")
    val sourceName: String = submission.language.sourceFilename ?: "src"
    val targetName: String = submission.language.targetFilename ?: "main"
    val compileBody = getCompileBody(submission.language, submission.code, sourceName, targetName)
    val result = Client.Sandbox.run(compileBody)
    if (result.size != 1) {
        setSelfTestSubmissionResult(submission.submissionId, SubmissionStatus.SYSTEM_ERROR)
        return
    }
    if (result[0].status != SandboxStatus.Accepted) {
        val res = when (result[0].status) {
            SandboxStatus.InternalError -> SubmissionStatus.SYSTEM_ERROR
            else -> SubmissionStatus.COMPILE_ERROR
        }
        setSelfTestSubmissionResult(
            submission.submissionId,
            res,
            compileLog = if (res == SubmissionStatus.COMPILE_ERROR) result[0].files["stderr"] else null,
        )
        return
    }
    if (result[0].fileIds.size != 1) {
        setSelfTestSubmissionResult(submission.submissionId, SubmissionStatus.SYSTEM_ERROR)
        return
    }
    val fileId = result[0].fileIds[targetName]
    if (fileId == null) {
        setSelfTestSubmissionResult(submission.submissionId, SubmissionStatus.SYSTEM_ERROR)
        return
    }
    setSelfTestSubmissionJudgeStage(submission.submissionId, JudgeStage.JUDGING)
    LOGGER.info("Now judging #${submission.submissionId}")

    val judgeResult =
        TestcaseRunner.runSelfTest(targetName, submission, fileId, submission.input, submission.expectedOutput)
    LOGGER.info("Self test #${submission.submissionId} ok, result: ${judgeResult.status}")

    setSelfTestSubmissionResult(
        submission.submissionId,
        judgeResult.status,
        judgeResult.time.toInt(),
        judgeResult.memory.toInt(),
        output = judgeResult.output,
    )

    Client.Sandbox.deleteFile(fileId)
}

private suspend fun runSubmission(submission: GetSubmissionResponse) {
    val fileId: String?
    LOGGER.info("Received submission #${submission.submissionId}")
    setSubmissionJudgeStage(submission.submissionId, JudgeStage.COMPILING)
    LOGGER.info("Now compiling #${submission.submissionId}")
    val sourceName: String = submission.language.sourceFilename ?: "src"
    val targetName: String = submission.language.targetFilename ?: "main"
    val compileBody = getCompileBody(submission.language, submission.code, sourceName, targetName)
    val result = Client.Sandbox.run(compileBody)
    if (result.size != 1) {
        setSubmissionResult(submission.submissionId, SubmissionStatus.SYSTEM_ERROR)
        return
    }
    if (result[0].status != SandboxStatus.Accepted) {
        val res = when (result[0].status) {
            SandboxStatus.InternalError -> SubmissionStatus.SYSTEM_ERROR
            else -> SubmissionStatus.COMPILE_ERROR
        }
        setSubmissionResult(
            submission.submissionId,
            res,
            compileLog = if (res == SubmissionStatus.COMPILE_ERROR) result[0].files["stderr"] else null,
        )
        return
    }
    if (result[0].fileIds.size != 1) {
        setSubmissionResult(submission.submissionId, SubmissionStatus.SYSTEM_ERROR)
        return
    }
    fileId = result[0].fileIds[targetName]
    if (fileId == null) {
        setSubmissionResult(submission.submissionId, SubmissionStatus.SYSTEM_ERROR)
        return
    }
    setSubmissionJudgeStage(submission.submissionId, JudgeStage.JUDGING)
    LOGGER.info("Now judging #${submission.submissionId}")
    if (isDownloadNeeded(submission.testcase.fileId, submission.testcase.hash)) {
        downloadTestcase(submission.testcase.fileId)
        unzipTestcase(submission.testcase.fileId)
    }

    val judgeResult = TestcaseRunner.runTestcase(targetName, submission, fileId)
    LOGGER.info("Submission #${submission.submissionId} ok, result: ${judgeResult.status}")

    setSubmissionResult(
        submission.submissionId,
        judgeResult.status,
        judgeResult.maxTime.toInt(),
        judgeResult.maxMemory.toInt(),
        judgeResult.testcases,
    )

    Client.Sandbox.deleteFile(fileId)
}

private suspend fun unzipTestcase(testcase: Long) {
    removeOldTestcaseFolder(testcase)
    // unzip testcase
    val file = File("testcase/$testcase.zip")
    val target = Path("testcase/$testcase")
    target.createDirectory()
    // unzip file with java api
    val zipFile = withContext(Dispatchers.IO) {
        ZipFile(file)
    }
    zipFile.entries().asSequence().forEach { entry ->
        val targetFile = target.resolve(entry.name)
        if (entry.isDirectory) {
            targetFile.createDirectory()
        } else {
            if (!targetFile.parent.exists()) {
                targetFile.parent.createDirectory()
            }
            zipFile.getInputStream(entry).use { input ->
                targetFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
    }
}

private suspend fun setSelfTestSubmissionJudgeStage(submissionId: Long, judgeStage: JudgeStage) {
    val body = UpdateSelfTestSubmissionRequest(
        submissionId = submissionId,
        time = 0,
        memory = 0,
        judgeStage = judgeStage,
        result = SubmissionStatus.JUDGING,
    )
    Client.Backend.updateSelfTestSubmission(submissionId, body)
}

private suspend fun setSubmissionJudgeStage(submissionId: Long, judgeStage: JudgeStage) {
    val body = UpdateSubmissionRequest(
        submissionId = submissionId,
        time = 0,
        memory = 0,
        judgerId = Configuration.JUDGER_ID,
        judgeStage = judgeStage,
        result = SubmissionStatus.JUDGING,
    )
    Client.Backend.updateSubmission(submissionId, body)
}

private suspend fun setSelfTestSubmissionResult(
    submissionId: Long,
    result: SubmissionStatus,
    time: Int = 0,
    memory: Int = 0,
    compileLog: String? = null,
    output: String? = null,
) {
    val body = UpdateSelfTestSubmissionRequest(
        submissionId = submissionId,
        time = time,
        memory = memory,
        judgeStage = JudgeStage.FINISHED,
        result = result,
        compileLog = if (result == SubmissionStatus.COMPILE_ERROR) compileLog else null,
        output = output,
    )
    Client.Backend.updateSelfTestSubmission(submissionId, body)
}

private suspend fun setSubmissionResult(
    submissionId: Long,
    result: SubmissionStatus,
    time: Int = 0,
    memory: Int = 0,
    testcaseResult: List<TestcaseJudgeResult> = listOf(),
    compileLog: String? = null,
) {
    val body = UpdateSubmissionRequest(
        submissionId = submissionId,
        time = time,
        memory = memory,
        judgerId = Configuration.JUDGER_ID,
        judgeStage = JudgeStage.FINISHED,
        result = result,
        compileLog = if (result == SubmissionStatus.COMPILE_ERROR) compileLog else null,
        testcaseResult = testcaseResult,
    )
    Client.Backend.updateSubmission(submissionId, body)
}

private suspend fun downloadTestcase(testcase: Long) {
    // remove old testcase
    if (isTestcaseExists(testcase)) {
        File("testcase/$testcase.zip").delete()
    }
    removeOldTestcaseFolder(testcase)
    val file = File("testcase/$testcase.zip")
    // download testcase
    Client.Backend.getTestcase(testcase, file)
}

@OptIn(ExperimentalPathApi::class)
private fun removeOldTestcaseFolder(testcase: Long) {
    if (Path("testcase/$testcase").exists()) {
        Path("testcase/$testcase").deleteRecursively()
    }
}

private fun getTestcaseArchiveMD5(testcase: Long): String {
    val file = File("testcase/$testcase.zip").inputStream()
    return fileMd5(file)
}

private fun isTestcaseExists(testcase: Long): Boolean {
    // check whether testcase folder exists
    if (!Path("testcase").exists()) {
        Path("testcase").createDirectory()
    }
    // check whether testcase exists
    return Path("testcase/$testcase.zip").exists()
}

private fun isDownloadNeeded(testcase: Long, hash: String): Boolean {
    if (!isTestcaseExists(testcase)) {
        return true
    }
    val md5 = getTestcaseArchiveMD5(testcase)
    return md5.lowercase() != hash.lowercase()
}

private fun getCompileBody(
    language: LanguageDto,
    code: String,
    sourceName: String,
    targetName: String,
): SandboxRequest {
    if (language.compileCommand == null) {
        throw IllegalStateException("compile command is null")
    }
    val compileCommand = language.compileCommand!!
        .replace("{src}", sourceName)
        .replace("{target}", targetName)
    return SandboxRequest(
        cmd = listOf(
            Cmd(
                args = listOf("/usr/bin/bash", "-c", compileCommand),
                env = listOf("PATH=/usr/bin:/bin"),
                files = listOf(
                    MemoryFile(content = ""),
                    Collector(name = "stdout", max = 51_200), // 50 KB
                    Collector(name = "stderr", max = 51_200), // 50 KB
                ),
                cpuLimit = 10_000_000_000L,
                memoryLimit = 536_870_912L,
                procLimit = 50,
                copyIn = mapOf(
                    sourceName to MemoryFile(content = code),
                ),
                copyOut = listOf("stdout", "stderr"),
                copyOutCached = listOf(targetName),
                copyOutMax = 1L * 2 * 1024 * 1024, // 2 MB
            ),
        ),
    )
}
