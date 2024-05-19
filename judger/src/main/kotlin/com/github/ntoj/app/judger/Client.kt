package com.github.ntoj.app.judger

import com.fasterxml.jackson.databind.DeserializationFeature
import com.github.ntoj.app.judger.Configuration.SANDBOX_SERVER
import com.github.ntoj.app.judger.Configuration.SERVER_HOST
import com.github.ntoj.app.shared.model.JudgeStage
import com.github.ntoj.app.shared.model.JudgerRecordDto
import com.github.ntoj.app.shared.model.R
import com.github.ntoj.app.shared.model.SubmissionStatus
import com.github.ntoj.app.shared.model.UpdateRecordRequest
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
        suspend fun get(): JudgerRecordDto? {
            val response =
                client.get("$SERVER_HOST/judge_client/get") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer ${Configuration.token}")
                }
            if (response.status == Forbidden) {
                throw IllegalStateException("Token 无效")
            }
            if (response.status == NoContent) {
                return null
            }
            return response.body<R<JudgerRecordDto>>().data
        }

        suspend fun update(
            recordId: String,
            stage: JudgeStage?,
            status: SubmissionStatus?,
            request: UpdateRecordRequest?,
        ) {
            val param = StringBuilder()
            if (stage != null) {
                param.append("stage=$stage")
            }
            if (status != null) {
                if (param.isNotEmpty()) {
                    param.append("&")
                }
                param.append("status=$status")
            }
            client.patch("$SERVER_HOST/judge_client/update/$recordId?$param") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer ${Configuration.token}")

                if (request != null) {
                    setBody(request)
                }
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
                    header("Authorization", "Bearer ${Configuration.token}")
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

        data class LoginResponse(
            val token: String,
        )

        suspend fun getToken(): String? {
            val response =
                client.get("$SERVER_HOST/auth/login?username=${Configuration.USERNAME}&password=${Configuration.PASSWORD}") {
                    contentType(ContentType.Application.Json)
                    header("X-Judger-ID", Configuration.JUDGER_ID)
                }
            return response.body<R<LoginResponse>>().data?.token
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

        suspend fun deleteFile(fileId: String?) {
            if (fileId != null) {
                val url = "$SANDBOX_SERVER/file/$fileId"
                client.delete(url)
            }
        }

        suspend fun version(): SandboxVersion {
            val response = client.get("$SANDBOX_SERVER/version")
            return response.body()
        }
    }
}
