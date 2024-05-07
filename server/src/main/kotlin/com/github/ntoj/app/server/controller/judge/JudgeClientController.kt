package com.github.ntoj.app.server.controller.judge

import com.github.ntoj.app.server.config.system.LanguageMap
import com.github.ntoj.app.server.ext.from
import com.github.ntoj.app.server.ext.success
import com.github.ntoj.app.server.service.FileService
import com.github.ntoj.app.server.service.FileUploadService
import com.github.ntoj.app.server.service.ProblemService
import com.github.ntoj.app.server.service.SelfTestSubmissionService
import com.github.ntoj.app.server.service.SubmissionService
import com.github.ntoj.app.shared.model.GetSelfTestSubmissionResponse
import com.github.ntoj.app.shared.model.GetSubmissionResponse
import com.github.ntoj.app.shared.model.JudgeStage
import com.github.ntoj.app.shared.model.R
import com.github.ntoj.app.shared.model.SubmissionStatus
import com.github.ntoj.app.shared.model.TestcaseDto
import com.github.ntoj.app.shared.model.UpdateSelfTestSubmissionRequest
import com.github.ntoj.app.shared.model.UpdateSubmissionRequest
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/judge_client")
class JudgeClientController(
    val submissionService: SubmissionService,
    val selfTestSubmissionService: SelfTestSubmissionService,
    val fileUploadService: FileUploadService,
    val fileService: FileService,
    private val problemService: ProblemService,
    private val languages: LanguageMap,
) {
    @GetMapping("/ping")
    fun ping(): ResponseEntity<R<PingResponse>> {
        return R.success(200, "Pong", PingResponse("Pong"))
    }

    @GetMapping("/get_submission")
    fun getSubmission(): ResponseEntity<R<GetSubmissionResponse>> {
        val submission =
            submissionService.getPendingSubmissionAndSetJudging() ?: return R.success(204, "获取成功", null)
        val languageStructure = languages[submission.lang]
        if (languageStructure == null) {
            submission.status = SubmissionStatus.SYSTEM_ERROR
            submissionService.update(submission)
            return R.success(204, "获取成功", null)
        }
        return R.success(
            200,
            "获取成功",
            GetSubmissionResponse(
                submissionId = submission.submissionId!!,
                problemId = submission.problem?.problemId!!,
                code = submission.code!!,
                lang = languageStructure,
                testcase = TestcaseDto.from(submission.problem!!.testCases!!),
                timeLimit = submission.problem!!.timeLimit!!,
                memoryLimit = submission.problem!!.memoryLimit!!,
            ),
        )
    }

    @GetMapping("/get_self_test_submission")
    fun getSelfTestSubmission(): ResponseEntity<R<GetSelfTestSubmissionResponse>> {
        val submission =
            selfTestSubmissionService.getPendingSubmissionAndSetJudging() ?: return R.success(204, "获取成功")
        val languageStructure = languages[submission.lang]
        if (languageStructure == null) {
            submission.status = SubmissionStatus.SYSTEM_ERROR
            selfTestSubmissionService.update(submission)
            return R.success(204, "获取成功", null)
        }
        return R.success(
            200,
            "获取成功",
            GetSelfTestSubmissionResponse(
                submissionId = submission.selfTestSubmissionId!!,
                code = submission.code,
                lang = languageStructure,
                timeLimit = submission.timeLimit,
                memoryLimit = submission.memoryLimit,
                input = submission.input,
                expectedOutput = submission.expectedOutput,
            ),
        )
    }

    @PatchMapping("/update_submission/{submissionId}")
    fun updateSubmission(
        @PathVariable submissionId: Long,
        @RequestBody submissionStatus: UpdateSubmissionRequest,
    ): ResponseEntity<R<Void>> {
        val submission = submissionService.get(submissionId)
        val problem = problemService.get(submission.problem?.problemId!!)
        if (submissionStatus.judgeStage == JudgeStage.FINISHED) {
            submission.status = submissionStatus.result
            submission.time = submissionStatus.time
            submission.memory = submissionStatus.memory
            submission.judgerId = submissionStatus.judgerId
            submission.testcaseResult = submissionStatus.testcaseResult
            submission.compileLog = submissionStatus.compileLog
            if (submissionStatus.result == SubmissionStatus.ACCEPTED && submission.contestId == null) {
                problem.acceptedTimes++
                problemService.update(problem)
            }
        }
        submission.judgeStage = submissionStatus.judgeStage
        submissionService.update(submission)
        return R.success(200, "更新成功")
    }

    @PatchMapping("/update_self_test_submission/{submissionId}")
    fun updateSelfTestSubmission(
        @PathVariable submissionId: Long,
        @RequestBody submissionStatus: UpdateSelfTestSubmissionRequest,
    ): ResponseEntity<R<Void>> {
        val submission = selfTestSubmissionService.get(submissionId)
        if (submissionStatus.judgeStage == JudgeStage.FINISHED) {
            submission.status = submissionStatus.result
            submission.time = submissionStatus.time
            submission.memory = submissionStatus.memory
            submission.compileLog = submissionStatus.compileLog
            submission.output = submissionStatus.output
        }
        submission.judgeStage = submissionStatus.judgeStage
        selfTestSubmissionService.update(submission)
        return R.success(200, "更新成功")
    }

    @GetMapping("/download_testcase/{id}")
    fun getTestcase(
        @PathVariable id: Long,
    ): ResponseEntity<Resource> {
        val testcase = fileUploadService.get(id)
        val file = fileService.get(testcase.path)
        val resource = InputStreamResource(file.inputStream())
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=${testcase.filename}")
            .contentType(MediaType.parseMediaType("application/zip"))
            .body(resource)
    }
}

data class PingResponse(
    val message: String,
    val currentTime: Instant = Instant.now(),
)
