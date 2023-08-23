package zip.ntoj.judger

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import zip.ntoj.shared.model.GetSubmissionResponse
import zip.ntoj.shared.model.JudgeStage
import zip.ntoj.shared.model.SubmissionStatus
import zip.ntoj.shared.model.TestcaseJudgeResult
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
    LOGGER.info("Server Host: ${Configuration.SERVER_HOST}")
    LOGGER.info("Sandbox Server: ${Configuration.SANDBOX_SERVER}")
    LOGGER.info("Judger ID: ${Configuration.JUDGER_ID}")
    LOGGER.info("Token: ${Configuration.TOKEN}")
}

suspend fun main() {
    showMessage()
    var connected = false
    while (true) {
        var fileId: String?
        try {
            val submission = Client.Backend.getSubmission()
            if (!connected) {
                LOGGER.info("连接成功，正在监听提交")
            }
            connected = true
            if (submission == null) {
                // no content sleep 1 s
                sleep(1000)
                continue
            }
            LOGGER.info("收到提交 ${submission.submissionId}")
            setSubmissionJudgeStage(submission.submissionId, JudgeStage.COMPILING)
            LOGGER.info("开始编译 ${submission.submissionId}")
            val sourceName: String = submission.language.sourceFilename ?: "src"
            val targetName: String = submission.language.targetFilename ?: "main"
            val compileBody = getCompileBody(submission, sourceName, targetName)
            val result = Client.Sandbox.run(compileBody)
            if (result.size != 1) {
                setSubmissionResult(submission.submissionId, SubmissionStatus.SYSTEM_ERROR)
                continue
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
                continue
            }
            if (result[0].fileIds.size != 1) {
                setSubmissionResult(submission.submissionId, SubmissionStatus.SYSTEM_ERROR)
                continue
            }
            fileId = result[0].fileIds[targetName]
            if (fileId == null) {
                setSubmissionResult(submission.submissionId, SubmissionStatus.SYSTEM_ERROR)
                continue
            }
            setSubmissionJudgeStage(submission.submissionId, JudgeStage.JUDGING)
            LOGGER.info("开始评测 ${submission.submissionId}")
            if (isDownloadNeeded(submission.testcase.fileId, submission.testcase.hash)) {
                downloadTestcase(submission.testcase.fileId)
                unzipTestcase(submission.testcase.fileId)
            }

            val judgeResult = TestcaseRunner.runTestcase(targetName, submission, fileId)
            LOGGER.info("评测完成 ${submission.submissionId}，结果：${judgeResult.status}")

            setSubmissionResult(
                submission.submissionId,
                judgeResult.status,
                judgeResult.maxTime.toInt(),
                judgeResult.maxMemory.toInt(),
                judgeResult.testcases,
            )

            Client.Sandbox.deleteFile(fileId)
        } catch (e: IllegalStateException) {
            throw e
        } catch (e: ConnectException) {
            connected = false
            e.printStackTrace()
            LOGGER.error("服务器连接失败，5秒后重试")
            sleep(5000)
        } catch (e: Exception) {
            connected = false
            LOGGER.error("未知错误", e)
        } finally {
            sleep(1000)
        }
    }
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

private suspend fun sleep(ms: Long) {
    withContext(Dispatchers.IO) {
        Thread.sleep(ms)
    }
}

private fun getCompileBody(
    submission: GetSubmissionResponse,
    sourceName: String,
    targetName: String,
): SandboxRequest {
    if (submission.language.compileCommand == null) {
        throw IllegalStateException("compile command is null")
    }
    val compileCommand = submission.language.compileCommand!!
        .replace("{src}", sourceName)
        .replace("{target}", targetName)
    return SandboxRequest(
        cmd = listOf(
            Cmd(
                args = listOf("/usr/bin/bash", "-c", compileCommand),
                env = listOf("PATH=/usr/bin:/bin"),
                files = listOf(
                    MemoryFile(content = ""),
                    Collector(name = "stdout", max = 10240),
                    Collector(name = "stderr", max = 10240),
                ),
                cpuLimit = 10_000_000_000L,
                memoryLimit = 536_870_912L,
                procLimit = 50,
                copyIn = mapOf(
                    sourceName to MemoryFile(content = submission.code),
                ),
                copyOut = listOf("stdout", "stderr"),
                copyOutCached = listOf(targetName),
            ),
        ),
    )
}
