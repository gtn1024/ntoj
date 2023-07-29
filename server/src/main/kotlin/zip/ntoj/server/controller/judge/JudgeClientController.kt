package zip.ntoj.server.controller.judge

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import zip.ntoj.server.model.R
import zip.ntoj.server.model.Submission
import zip.ntoj.server.service.SubmissionService
import zip.ntoj.shared.dtos.judge.GetSubmissionResponse
import java.time.Instant

@RestController
@RequestMapping("/judge_client")
class JudgeClientController(
    val submissionService: SubmissionService,
) {
    @GetMapping("/ping")
    fun ping(): ResponseEntity<R<PingResponse>> {
        return R.success(200, "Pong", PingResponse("Pong"))
    }

    @GetMapping("/get_submission")
    fun getSubmission(): ResponseEntity<R<GetSubmissionResponse>> {
        var submission = submissionService.getPendingSubmission() ?: return R.success(204, "获取成功", null)
        submission.status = Submission.SubmissionStatus.JUDGING
        submission = submissionService.update(submission)
        return R.success(
            200,
            "获取成功",
            GetSubmissionResponse(
                submissionId = submission.submissionId!!,
                problemId = submission.problem?.problemId!!,
                code = submission.code!!,
                language = submission.language!!,
            ),
        )
    }
}

data class PingResponse(
    val message: String,
    val currentTime: Instant = Instant.now(),
)
