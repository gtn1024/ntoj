package zip.ntoj.server.controller.judge

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
import zip.ntoj.server.controller.admin.TestcaseDto
import zip.ntoj.server.model.Language
import zip.ntoj.server.model.R
import zip.ntoj.server.model.Submission
import zip.ntoj.server.service.FileService
import zip.ntoj.server.service.FileUploadService
import zip.ntoj.server.service.SubmissionService
import zip.ntoj.shared.dtos.judge.SubmissionStatus
import java.time.Instant

@RestController
@RequestMapping("/judge_client")
class JudgeClientController(
    val submissionService: SubmissionService,
    val fileUploadService: FileUploadService,
    val fileService: FileService,
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
                language = GetSubmissionResponse.LanguageDto.from(submission.language!!),
                testcase = TestcaseDto.from(submission.problem!!.testCases!!),
                timeLimit = submission.problem!!.timeLimit!!,
                memoryLimit = submission.problem!!.memoryLimit!!,
            ),
        )
    }

    @PatchMapping("/update_submission/{submissionId}")
    fun updateSubmission(
        @PathVariable submissionId: Long,
        @RequestBody submissionStatus: SubmissionJudgeResult,
    ): ResponseEntity<R<Void>> {
        val submission = submissionService.get(submissionId)
        if (submissionStatus.judgeStage == Submission.JudgeStage.FINISHED) {
            submission.status = submissionStatus.result
            submission.time = submissionStatus.time
            submission.memory = submissionStatus.memory
            submission.judgerId = submissionStatus.judgerId
        }
        submission.judgeStage = submissionStatus.judgeStage
        submissionService.update(submission)
        return R.success(200, "更新成功")
    }

    @GetMapping("/download_testcase/{id}")
    fun getTestcase(@PathVariable id: Long): ResponseEntity<Resource> {
        val testcase = fileUploadService.get(id)
        val file = fileService.get(testcase.path)
        val resource = InputStreamResource(file.inputStream())
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=${testcase.filename}")
            .contentType(MediaType.parseMediaType("application/zip"))
            .body(resource)
    }

    data class GetSubmissionResponse(
        val submissionId: Long,
        val problemId: Long,
        val code: String,
        val language: LanguageDto,
        val testcase: TestcaseDto,
        val timeLimit: Int,
        val memoryLimit: Int,
    ) {
        data class LanguageDto(
            val languageId: Long,
            val name: String,
            val compileCommand: String?,
            val executeCommand: String?,
            val type: Language.LanguageType,
        ) {
            companion object {
                fun from(language: Language) = LanguageDto(
                    languageId = language.languageId!!,
                    name = language.languageName,
                    compileCommand = language.compileCommand,
                    executeCommand = language.executeCommand,
                    type = language.type,
                )
            }
        }
    }

    data class SubmissionJudgeResult(
        val submissionId: Long,
        val problemId: Long,
        val result: SubmissionStatus,
        val time: Int?,
        val memory: Int?,

        val judgerId: String? = null,
        val judgeStage: Submission.JudgeStage,
    )
}

data class PingResponse(
    val message: String,
    val currentTime: Instant = Instant.now(),
)
