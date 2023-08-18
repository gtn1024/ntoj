package zip.ntoj.judger

import com.fasterxml.jackson.annotation.JsonCreator
import zip.ntoj.shared.model.SubmissionStatus
import zip.ntoj.shared.model.TestcaseJudgeResult

data class SandboxRequest(
    val requestId: String? = null,
    val cmd: List<Cmd> = listOf(),
    val pipeMapping: List<PipeMap> = listOf(),
)

data class Cmd(
    val args: List<String>,
    val env: List<String> = listOf(),
    val files: List<CmdFile> = listOf(),
    val tty: Boolean? = null,
    val cpuLimit: Long? = null,
    val clockLimit: Long? = null,
    val memoryLimit: Long? = null,
    val stackLimit: Long? = null,
    val procLimit: Long? = null,
    val cpuRateLimit: Long? = null,
    val cpuSetLimit: String? = null,
    val strictMemoryLimit: Boolean? = null,
    val copyIn: Map<String, CopyInFile> = mapOf(),
    val copyOut: List<String> = listOf(),
    val copyOutCached: List<String> = listOf(),
    val copyOutMax: Long? = null,
)

data class PipeMap(
    val `in`: PipeIndex,
    val out: PipeIndex,
    val proxy: Boolean? = null,
    val name: String? = null,
    val max: Long? = null,
)

data class PipeIndex(
    val index: Long,
    val fd: Long,
)

interface CmdFile

data class LocalFile(
    val src: String,
) : CmdFile, CopyInFile

data class MemoryFile(
    val content: String,
) : CmdFile, CopyInFile

data class PreparedFile(
    val fileId: String,
) : CmdFile, CopyInFile

data class Collector(
    val name: String,
    val max: Long,
    val pipe: Boolean? = null,
) : CmdFile

interface CopyInFile

enum class SandboxStatus(msg: String) {
    Accepted("Accepted"), // 正常情况
    MemoryLimitExceeded("Memory Limit Exceeded"), // 内存超限
    TimeLimitExceeded("Time Limit Exceeded"), // 时间超限
    OutputLimitExceeded("Output Limit Exceeded"), // 输出超限
    FileError("File Error"), // 文件错误
    NonzeroExitStatus("Nonzero Exit Status"), // 非 0 退出值
    Signalled("Signalled"), // 进程被信号终止
    InternalError("Internal Error"), // 内部错误
    ;

    companion object {
        @JsonCreator
        @JvmStatic
        fun fromMsg(msg: String): SandboxStatus {
            return when (msg) {
                "Accepted" -> Accepted
                "Memory Limit Exceeded" -> MemoryLimitExceeded
                "Time Limit Exceeded" -> TimeLimitExceeded
                "Output Limit Exceeded" -> OutputLimitExceeded
                "File Error" -> FileError
                "Nonzero Exit Status" -> NonzeroExitStatus
                "Signalled" -> Signalled
                "Internal Error" -> InternalError
                else -> throw IllegalArgumentException("Unknown message: $msg")
            }
        }
    }
}

data class SandboxResult(
    val status: SandboxStatus,
    val error: String? = null,
    val exitStatus: Long,
    val time: Long,
    val memory: Long,
    val runTime: Long,
    val files: Map<String, String> = mapOf(),
    val fileIds: Map<String, String> = mapOf(),
    val fileError: List<FileError> = listOf(),
)

data class FileError(
    val name: String,
    val type: FileErrorType,
    val message: String? = null,
)

enum class FileErrorType {
    CopyInOpenFile,
    CopyInCreateFile,
    CopyInCopyContent,
    CopyOutOpen,
    CopyOutNotRegularFile,
    CopyOutSizeExceeded,
    CopyOutCreateFile,
    CopyOutCopyContent,
    CollectSizeExceeded,
}

data class SandboxVersion(
    val buildVersion: String,
    val goVersion: String,
    val platform: String,
    val os: String,
    val copyOutOptional: Boolean? = null,
    val pipeProxy: Boolean? = null,
)

data class JudgeResult(
    val testcases: List<TestcaseJudgeResult> = listOf(),
    val status: SubmissionStatus,
    val maxTime: Long,
    val maxMemory: Long,
)
