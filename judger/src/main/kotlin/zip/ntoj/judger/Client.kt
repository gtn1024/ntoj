package zip.ntoj.judger

import com.fasterxml.jackson.databind.DeserializationFeature
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.Forbidden
import io.ktor.http.HttpStatusCode.Companion.NoContent
import io.ktor.http.contentType
import io.ktor.serialization.jackson.jackson
import io.ktor.utils.io.core.isEmpty
import io.ktor.utils.io.core.readBytes
import zip.ntoj.judger.Configuration.SANDBOX_SERVER
import zip.ntoj.judger.Configuration.SERVER_HOST
import zip.ntoj.shared.model.GetSelfTestSubmissionResponse
import zip.ntoj.shared.model.GetSubmissionResponse
import zip.ntoj.shared.model.R
import zip.ntoj.shared.model.UpdateSelfTestSubmissionRequest
import zip.ntoj.shared.model.UpdateSubmissionRequest
import java.io.File

object Client {
    private val client =
        HttpClient(CIO) {
            install(ContentNegotiation) {
                jackson {
                    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                }
            }
        }

    object Backend {
        suspend fun getSubmission(): GetSubmissionResponse? {
            val response =
                client.get("$SERVER_HOST/judge_client/get_submission") {
                    contentType(ContentType.Application.Json)
                    header("X-Judger-Token", Configuration.TOKEN)
                    header("X-Judger-ID", Configuration.JUDGER_ID)
                    header("X-Judger-OS", Configuration.OS)
                    header("X-Judger-Kernel", Configuration.KERNEL)
                    header("X-Judger-Memory-Used", Configuration.memoryUsed().toString())
                    header("X-Judger-Memory-Total", Configuration.memoryTotal().toString())
                }
            if (response.status == Forbidden) {
                throw IllegalStateException("Token 无效")
            }
            if (response.status == NoContent) {
                return null
            }
            return response.body<R<GetSubmissionResponse>>().data
        }

        suspend fun getSelfTestSubmission(): GetSelfTestSubmissionResponse? {
            val response =
                client.get("$SERVER_HOST/judge_client/get_self_test_submission") {
                    contentType(ContentType.Application.Json)
                    header("X-Judger-Token", Configuration.TOKEN)
                    header("X-Judger-ID", Configuration.JUDGER_ID)
                    header("X-Judger-OS", Configuration.OS)
                    header("X-Judger-Kernel", Configuration.KERNEL)
                    header("X-Judger-Memory-Used", Configuration.memoryUsed().toString())
                    header("X-Judger-Memory-Total", Configuration.memoryTotal().toString())
                }
            if (response.status == Forbidden) {
                throw IllegalStateException("Token 无效")
            }
            if (response.status == NoContent) {
                return null
            }
            return response.body<R<GetSelfTestSubmissionResponse>>().data
        }

        suspend fun updateSubmission(
            submissionId: Long,
            submissionStatus: UpdateSubmissionRequest,
        ) {
            client.patch("$SERVER_HOST/judge_client/update_submission/$submissionId") {
                contentType(ContentType.Application.Json)
                header("X-Judger-Token", Configuration.TOKEN)
                header("X-Judger-ID", Configuration.JUDGER_ID)
                header("X-Judger-OS", Configuration.OS)
                header("X-Judger-Kernel", Configuration.KERNEL)
                header("X-Judger-Memory-Used", Configuration.memoryUsed().toString())
                header("X-Judger-Memory-Total", Configuration.memoryTotal().toString())
                setBody(submissionStatus)
            }
        }

        suspend fun updateSelfTestSubmission(
            submissionId: Long,
            submissionStatus: UpdateSelfTestSubmissionRequest,
        ) {
            client.patch("$SERVER_HOST/judge_client/update_self_test_submission/$submissionId") {
                contentType(ContentType.Application.Json)
                header("X-Judger-Token", Configuration.TOKEN)
                header("X-Judger-ID", Configuration.JUDGER_ID)
                header("X-Judger-OS", Configuration.OS)
                header("X-Judger-Kernel", Configuration.KERNEL)
                header("X-Judger-Memory-Used", Configuration.memoryUsed().toString())
                header("X-Judger-Memory-Total", Configuration.memoryTotal().toString())
                setBody(submissionStatus)
            }
        }

        suspend fun getTestcase(
            fileId: Long,
            file: File,
        ) {
            val url = "$SERVER_HOST/judge_client/download_testcase/$fileId"
            val channel =
                client.get(url) {
                    contentType(ContentType.Application.Json)
                    header("X-Judger-Token", Configuration.TOKEN)
                    header("X-Judger-ID", Configuration.JUDGER_ID)
                    header("X-Judger-OS", Configuration.OS)
                    header("X-Judger-Kernel", Configuration.KERNEL)
                    header("X-Judger-Memory-Used", Configuration.memoryUsed().toString())
                    header("X-Judger-Memory-Total", Configuration.memoryTotal().toString())
                }.bodyAsChannel()
            while (!channel.isClosedForRead) {
                val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                while (!packet.isEmpty) {
                    val bytes = packet.readBytes()
                    file.appendBytes(bytes)
                }
            }
        }
    }

    object Sandbox {
        suspend fun run(req: SandboxRequest): List<SandboxResult> {
            val response =
                client.post("$SANDBOX_SERVER/run") {
                    contentType(ContentType.Application.Json)
                    setBody(req)
                }
            return response.body()
        }

        suspend fun deleteFile(fileId: String) {
            val url = "$SANDBOX_SERVER/file/$fileId"
            client.delete(url)
        }

        suspend fun version(): SandboxVersion {
            val response = client.get("$SANDBOX_SERVER/version")
            return response.body()
        }
    }
}
