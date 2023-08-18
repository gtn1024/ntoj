package zip.ntoj.judger

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import zip.ntoj.shared.model.GetSubmissionResponse
import zip.ntoj.shared.model.JudgeStage
import zip.ntoj.shared.model.LanguageType
import zip.ntoj.shared.model.SubmissionStatus
import zip.ntoj.shared.model.UpdateSubmissionRequest
import zip.ntoj.shared.util.ZipUtils
import zip.ntoj.shared.util.fileMd5
import zip.ntoj.shared.util.removeLastEmptyLine
import zip.ntoj.shared.util.trimByLine
import java.io.File
import java.net.ConnectException
import java.util.zip.ZipFile
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.deleteRecursively
import kotlin.io.path.exists
import kotlin.io.path.outputStream

suspend fun main() {
    var connected = false
    while (true) {
        var fileId: String? = null
        try {
            val submission = Client.Backend.getSubmission()
            if (!connected) {
                println("连接成功，正在监听提交")
            }
            connected = true
            if (submission == null) {
                // no content sleep 1 s
                sleep(1000)
                continue
            }
            println("收到提交 ${submission.submissionId}")
            val sourceName: String = when (submission.language.type) {
                LanguageType.C -> SourceFilename.C
                LanguageType.CPP -> SourceFilename.CPP
                LanguageType.PYTHON -> SourceFilename.PYTHON
                LanguageType.JAVA -> SourceFilename.JAVA
                LanguageType.OTHER -> throw IllegalStateException("不支持的语言类型")
            }
            val targetName: String = when (submission.language.type) {
                LanguageType.C -> TargetFilename.C
                LanguageType.CPP -> TargetFilename.CPP
                LanguageType.PYTHON -> TargetFilename.PYTHON
                LanguageType.JAVA -> TargetFilename.JAVA
                LanguageType.OTHER -> throw IllegalStateException("不支持的语言类型")
            }
            val compileBody = getCompileBody(submission, sourceName, targetName)
            val result = Client.Sandbox.run(compileBody)
            if (result.size != 1) {
                println("result.size != 1")
                continue
            }
            if (result[0].status != SandboxStatus.Accepted) {
                val body = UpdateSubmissionRequest(
                    submissionId = submission.submissionId,
                    problemId = submission.problemId,
                    time = 0,
                    memory = 0,
                    judgerId = Configuration.JUDGER_ID,
                    judgeStage = JudgeStage.FINISHED,
                    result = when (result[0].status) {
                        SandboxStatus.InternalError -> SubmissionStatus.SYSTEM_ERROR
                        else -> SubmissionStatus.COMPILE_ERROR
                    },
                )
                Client.Backend.updateSubmission(submission.submissionId, body)
                continue
            }
            if (result[0].fileIds.size != 1) {
                println("result[0].fileIds.size != 1")
                continue
            }
            fileId = result[0].fileIds[targetName]
            if (fileId == null) {
                println("fileId == null")
                continue
            }
            println("编译结果 $fileId")
            if (isDownloadNeeded(submission.testcase.fileId, submission.testcase.hash)) {
                downloadTestcase(submission.testcase.fileId)
            }
            unzipTestcase(submission.testcase.fileId)

            val judgeResult = runTestcase(targetName, submission, fileId)

            val body = UpdateSubmissionRequest(
                submissionId = submission.submissionId,
                problemId = submission.problemId,
                time = judgeResult.time.toInt(),
                memory = judgeResult.memory.toInt(),
                judgerId = Configuration.JUDGER_ID,
                judgeStage = JudgeStage.FINISHED,
                result = judgeResult.status,
            )
            Client.Backend.updateSubmission(submission.submissionId, body)

            Client.Sandbox.deleteFile(fileId)
        } catch (e: ConnectException) {
            connected = false
            e.printStackTrace()
            println("服务器连接失败，5秒后重试")
            sleep(5000)
        } catch (e: Exception) {
            connected = false
            println("未知错误")
            e.printStackTrace()
        } finally {
            sleep(1000)
        }
    }
}

private suspend fun getTestcaseNumber(testcase: Long): Int {
    return ZipUtils.getFilenamesFromZip(File("testcase/$testcase.zip")).size / 2
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

private data class JudgeResult(
    val status: SubmissionStatus,
    val time: Long,
    val memory: Long,
)

private suspend fun runTestcase(
    targetName: String,
    submission: GetSubmissionResponse,
    fileId: String,
): JudgeResult {
    val number = getTestcaseNumber(submission.testcase.fileId)
    var time = 0L
    var memory = 0L
    for (i in 1..number) {
        val inData = File("testcase/${submission.testcase.fileId}/$i.in").readText()
        val body = getRunBody(submission, targetName, inData, fileId)
        val result = Client.Sandbox.run(body)
        if (result.size != 1) {
            println("result.size != 1")
            return JudgeResult(SubmissionStatus.SYSTEM_ERROR, 0, 0)
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
            return JudgeResult(st, 0, 0)
        }
        memory = memory.coerceAtLeast(res.memory / 1024)
        time = time.coerceAtLeast(res.runTime / 1000 / 1000)
        val stdout = File("testcase/${submission.testcase.fileId}/$i.out").readText()
            .trimByLine()
            .removeLastEmptyLine()

        if (res.files["stdout"]?.trimByLine()?.removeLastEmptyLine() != stdout) {
            return JudgeResult(SubmissionStatus.WRONG_ANSWER, 0, 0)
        }
        println(result)
    }
    return JudgeResult(SubmissionStatus.ACCEPTED, time, memory)
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

private fun getCompileBody(submission: GetSubmissionResponse, sourceName: String, targetName: String): SandboxRequest {
    val compileCommand = submission.language.compileCommand!!
        .replace("{src}", sourceName)
        .replace("{target}", targetName)
    return SandboxRequest(
        cmd = listOf(
            Cmd(
                args = compileCommand.split(" "),
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
                args = executeCommand.split(" "),
                env = listOf("PATH=/usr/bin:/bin"),
                files = listOf(
                    MemoryFile(inData),
                    Collector(name = "stdout", max = 10240),
                    Collector(name = "stderr", max = 10240),
                ),
                cpuLimit = 1L * submission.timeLimit * 1000 * 1000,
                clockLimit = 1L * submission.timeLimit * 1000 * 1000 * 2,
                memoryLimit = 1L * submission.memoryLimit * 1024,
                stackLimit = 1L * submission.memoryLimit * 1024,
                procLimit = 50,
                copyIn = mapOf(
                    targetName to PreparedFile(fileId),
                ),
            ),
        ),
    )
}
