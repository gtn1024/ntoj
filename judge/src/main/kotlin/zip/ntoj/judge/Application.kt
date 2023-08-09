package zip.ntoj.judge

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.jackson.jackson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import zip.ntoj.judge.config.NTOJ_CLIENT_ID
import zip.ntoj.judge.config.NTOJ_SERVER_URL
import zip.ntoj.shared.dtos.judge.GetSubmissionResponse
import zip.ntoj.shared.dtos.judge.SubmissionJudgeResult
import zip.ntoj.shared.dtos.judge.SubmissionStatus
import java.util.Random

val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        jackson()
    }
}

val logger = LoggerFactory.getLogger("zip.ntoj.judge.Application")

suspend fun main() {
    while (true) {
        try {
            val submission = getSubmission()
            if (submission == null) {
                withContext(Dispatchers.IO) {
                    Thread.sleep(1000)
                }
                continue
            }
            logger.debug("get submission: {}", submission)
            val result = judge(submission)
            updateSubmission(result)
        } catch (e: Exception) {
            logger.error("judge failed", e)
        }
    }
}

suspend fun getSubmission(): GetSubmissionResponse? {
    val response = client.get("$NTOJ_SERVER_URL/judge_client/get_submission")
    if (response.status == HttpStatusCode.NoContent) {
        return null
    }
    val body: R<GetSubmissionResponse> = response.body()
    logger.debug(body.toString())
    return body.data
}

fun judge(submission: GetSubmissionResponse): SubmissionJudgeResult {
    // TODO: judge
    return SubmissionJudgeResult(
        submissionId = submission.submissionId,
        problemId = submission.problemId,
        code = submission.code,
        language = submission.language,
        result = SubmissionStatus.entries[Random().nextInt(0, 8) + 2],
        time = 0,
        memory = 0,

        judgerId = NTOJ_CLIENT_ID,
    )
}

suspend fun updateSubmission(result: SubmissionJudgeResult) {
    val response = client.patch("$NTOJ_SERVER_URL/judge_client/update_submission/${result.submissionId}") {
        contentType(ContentType.Application.Json)
        setBody(result)
    }
    logger.debug(response.bodyAsText())
}
