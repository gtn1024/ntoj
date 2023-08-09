package zip.ntoj.server.controller.judge

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import zip.ntoj.server.model.R
import zip.ntoj.server.model.Submission
import zip.ntoj.server.service.SubmissionService
import zip.ntoj.shared.dtos.judge.GetSubmissionResponse
import zip.ntoj.shared.dtos.judge.SubmissionJudgeResult
import zip.ntoj.shared.dtos.judge.SubmissionStatus
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
        submission.status = SubmissionStatus.JUDGING
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

    @PatchMapping("/update_submission/{submissionId}")
    fun updateSubmission(
        @PathVariable submissionId: Long,
        @RequestBody submissionStatus: SubmissionJudgeResult,
    ): ResponseEntity<R<Void>> {
        val submission = submissionService.get(submissionId)
        submission.status = submissionStatus.result
        submission.time = submissionStatus.time
        submission.memory = submissionStatus.memory
        submission.judgerId = submissionStatus.judgerId
        submissionService.update(submission)
        return R.success(200, "更新成功")
    }
}

data class PingResponse(
    val message: String,
    val currentTime: Instant = Instant.now(),
)
