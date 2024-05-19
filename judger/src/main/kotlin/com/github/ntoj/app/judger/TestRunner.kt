package com.github.ntoj.app.judger

import com.github.ntoj.app.shared.model.JudgeStage
import com.github.ntoj.app.shared.model.LanguageStructure
import com.github.ntoj.app.shared.model.RecordOrigin
import com.github.ntoj.app.shared.model.SubmissionStatus
import com.github.ntoj.app.shared.model.TestcaseJudgeResult
import com.github.ntoj.app.shared.model.UpdateRecordRequest
import com.github.ntoj.app.shared.util.ZipUtils
import com.github.ntoj.app.shared.util.fileMd5
import com.github.ntoj.app.shared.util.removeLastEmptyLine
import com.github.ntoj.app.shared.util.trimByLine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.util.zip.ZipFile
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.deleteRecursively
import kotlin.io.path.exists
import kotlin.io.path.outputStream

object TestRunner {
    private val Logger: Logger = LoggerFactory.getLogger(TestRunner::class.java)

    suspend fun runTest(
        recordId: String,
        code: String,
        lang: LanguageStructure,
        timeLimit: Int,
        memoryLimit: Int,
        origin: RecordOrigin,
        input: String?,
        testcaseFileId: Long?,
        testcaseHash: String?,
    ) {
        val (_, execute, _, compileTimeLimit, _, compile, _, source, target, timeLimitRate, memoryLimitRate) = lang
        var fileId: String? = null

        if (!compile.isNullOrBlank()) {
            fileId = runCompileStage(recordId, code, source, target, compile, compileTimeLimit?.toInt() ?: 30)
            if (fileId == null) {
                return
            }
        }

        val result =
            runJudgeStage(
                recordId,
                code,
                fileId,
                origin,
                input,
                target,
                execute,
                timeLimit,
                memoryLimit,
                timeLimitRate.toInt(),
                memoryLimitRate.toInt(),
                testcaseFileId,
                testcaseHash,
            )
        val request =
            UpdateRecordRequest(
                time = result.maxTime.toInt(),
                memory = result.maxMemory.toInt(),
                testcaseResult = result.testcases,
                judgerId = Configuration.JUDGER_ID,
            )
        Client.Backend.update(recordId, JudgeStage.FINISHED, result.status, request)
        Logger.info("Record {} finished", recordId)

        Client.Sandbox.deleteFile(fileId)
    }

    private suspend fun runCompileStage(
        recordId: String,
        code: String,
        source: String,
        target: String,
        compile: String,
        compileTimeLimit: Int,
    ): String? {
        Logger.info("Compiling {}", recordId)
        Client.Backend.update(recordId, JudgeStage.COMPILING, SubmissionStatus.JUDGING, null)
        val (fileId, compileError) = compile(code, source, target, compile, compileTimeLimit)
        if (fileId == null) {
            Logger.error("Compile failed: {}", recordId)
            Client.Backend.update(
                recordId,
                JudgeStage.FINISHED,
                SubmissionStatus.COMPILE_ERROR,
                UpdateRecordRequest(null, null, compileError, Configuration.JUDGER_ID, listOf()),
            )
            return null
        }
        return fileId
    }

    private suspend fun runJudgeStage(
        recordId: String,
        code: String,
        fileId: String?,
        origin: RecordOrigin,
        input: String?,
        target: String,
        execute: String,
        timeLimit: Int,
        memoryLimit: Int,
        timeLimitRate: Int,
        memoryLimitRate: Int,
        testcaseFileId: Long?,
        testcaseHash: String?,
    ): JudgeResult {
        Client.Backend.update(recordId, JudgeStage.JUDGING, SubmissionStatus.JUDGING, null)

        var maxTime = 0L
        var maxMemory = 0L
        var judgeResult = SubmissionStatus.ACCEPTED
        val testcaseJudgeResults = mutableListOf<TestcaseJudgeResult>()

        if (origin == RecordOrigin.SELF_TEST) {
            Logger.info("Judging self test")
            runSingleTestcase(
                origin,
                code,
                fileId,
                1,
                input!!,
                target,
                execute,
                timeLimit,
                memoryLimit,
                timeLimitRate,
                memoryLimitRate,
                null,
            ).let {
                testcaseJudgeResults.add(it)
                maxTime = maxTime.coerceAtLeast(it.time)
                maxMemory = maxMemory.coerceAtLeast(it.memory)
                if (it.status != SubmissionStatus.ACCEPTED) {
                    judgeResult = it.status
                }
            }
        } else {
            Logger.info("Judging testcases")
            if (isDownloadNeeded(testcaseFileId!!, testcaseHash!!)) {
                downloadTestcase(testcaseFileId)
                unzipTestcase(testcaseFileId)
            }

            val number = getTestcaseNumber(testcaseFileId)
            for (i in 1..number) {
                val inData = File("testcase/$testcaseFileId/$i.in").readText()
                runSingleTestcase(
                    origin,
                    code,
                    fileId,
                    i,
                    inData,
                    target,
                    execute,
                    timeLimit,
                    memoryLimit,
                    timeLimitRate,
                    memoryLimitRate,
                    testcaseFileId,
                ).let {
                    testcaseJudgeResults.add(it)
                    maxTime = maxTime.coerceAtLeast(it.time)
                    maxMemory = maxMemory.coerceAtLeast(it.memory)
                    if (it.status != SubmissionStatus.ACCEPTED && judgeResult == SubmissionStatus.ACCEPTED) {
                        judgeResult = it.status
                    }
                }
            }
        }

        return JudgeResult(testcaseJudgeResults, judgeResult, maxTime, maxMemory)
    }

    private fun getTestcaseNumber(testcase: Long): Int {
        return ZipUtils.getFilenamesFromZip(File("testcase/$testcase.zip")).size / 2
    }

    @OptIn(ExperimentalPathApi::class)
    private fun removeOldTestcaseFolder(testcase: Long) {
        if (Path("testcase/$testcase").exists()) {
            Path("testcase/$testcase").deleteRecursively()
        }
    }

    private suspend fun unzipTestcase(testcase: Long) {
        removeOldTestcaseFolder(testcase)
        // unzip testcase
        val file = File("testcase/$testcase.zip")
        val target = Path("testcase/$testcase")
        target.createDirectory()
        // unzip file with java api
        val zipFile =
            withContext(Dispatchers.IO) {
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

    private fun isDownloadNeeded(
        testcase: Long,
        hash: String,
    ): Boolean {
        if (!isTestcaseExists(testcase)) {
            return true
        }
        val md5 = getTestcaseArchiveMD5(testcase)
        return md5.lowercase() != hash.lowercase()
    }

    private fun isTestcaseExists(testcase: Long): Boolean {
        // check whether testcase folder exists
        if (!Path("testcase").exists()) {
            Path("testcase").createDirectory()
        }
        // check whether testcase exists
        return Path("testcase/$testcase.zip").exists()
    }

    private fun getTestcaseArchiveMD5(testcase: Long): String {
        val file = File("testcase/$testcase.zip").inputStream()
        return fileMd5(file)
    }

    private suspend fun runSingleTestcase(
        origin: RecordOrigin,
        code: String,
        fileId: String?,
        idx: Int,
        inData: String,
        target: String,
        execute: String,
        timeLimit: Int,
        memoryLimit: Int,
        timeLimitRate: Int,
        memoryLimitRate: Int,
        testcaseFileId: Long?,
    ): TestcaseJudgeResult {
        val runBody =
            getRunBody(code, fileId, target, execute, inData, timeLimit, memoryLimit, timeLimitRate, memoryLimitRate)
        val result = Client.Sandbox.run(runBody)
        if (result.size != 1) {
            return TestcaseJudgeResult(SubmissionStatus.SYSTEM_ERROR, 0, 0, inData, null)
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
            return TestcaseJudgeResult(st, res.time / 1000 / 1000, res.memory / 1024, inData, null)
        }
        if (origin != RecordOrigin.SELF_TEST && testcaseFileId != null) {
            val stdout =
                File("testcase/$testcaseFileId/$idx.out").readText()
                    .trimByLine()
                    .removeLastEmptyLine()

            if (res.files["stdout"]?.trimByLine()?.removeLastEmptyLine() != stdout) {
                return TestcaseJudgeResult(
                    SubmissionStatus.WRONG_ANSWER,
                    res.time / 1000 / 1000,
                    res.memory / 1024,
                    inData,
                    res.files["stdout"],
                )
            }
        }
        return TestcaseJudgeResult(
            SubmissionStatus.ACCEPTED,
            res.time / 1000 / 1000,
            res.memory / 1024,
            inData,
            res.files["stdout"],
        )
    }

    data class CompileResult(
        val fileId: String?,
        val compileError: String?,
    )

    private suspend fun compile(
        code: String,
        source: String,
        target: String,
        compile: String,
        compileTimeLimit: Int,
    ): CompileResult {
        val compileBody = getCompileBody(code, source, target, compile, compileTimeLimit)
        val result = Client.Sandbox.run(compileBody)
        if (result.size != 1) {
            Logger.error("Compile failed: {}", result)
            return CompileResult(null, "Internal error")
        }
        if (result[0].status != SandboxStatus.Accepted) {
            Logger.error("Compile failed: {}", result)
            return CompileResult(null, result[0].files["stderr"])
        }
        if (result[0].fileIds.size != 1) {
            return CompileResult(null, "Internal error")
        }
        val fileId = result[0].fileIds[target]
        return CompileResult(fileId, null)
    }

    private fun getRunBody(
        code: String,
        fileId: String?,
        target: String,
        execute: String,
        inData: String,
        timeLimit: Int,
        memoryLimit: Int,
        timeLimitRate: Int,
        memoryLimitRate: Int,
    ): SandboxRequest {
        val runData = if (fileId == null) MemoryFile(code) else PreparedFile(fileId)
        return SandboxRequest(
            cmd =
                listOf(
                    Cmd(
                        args = listOf("/usr/bin/bash", "-c", execute),
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
                                target to runData,
                            ),
                        // 2 MB
                        copyOutMax = 1L * 2 * 1024 * 1024,
                    ),
                ),
        )
    }

    private fun getCompileBody(
        code: String,
        source: String,
        target: String,
        compile: String,
        compileTimeLimit: Int,
    ): SandboxRequest {
        return SandboxRequest(
            cmd =
                listOf(
                    Cmd(
                        args = listOf("/usr/bin/bash", "-c", compile),
                        env = listOf("PATH=/usr/bin:/bin"),
                        files =
                            listOf(
                                MemoryFile(content = ""),
                                // 50 KB
                                Collector(name = "stdout", max = 51_200),
                                // 50 KB
                                Collector(name = "stderr", max = 51_200),
                            ),
                        cpuLimit = compileTimeLimit * 1_000_000_000L,
                        memoryLimit = 536_870_912L,
                        procLimit = 50,
                        copyIn =
                            mapOf(
                                source to MemoryFile(content = code),
                            ),
                        copyOut = listOf("stdout", "stderr"),
                        copyOutCached = listOf(target),
                        // 2 MB
                        copyOutMax = 1L * 2 * 1024 * 1024,
                    ),
                ),
        )
    }
}
