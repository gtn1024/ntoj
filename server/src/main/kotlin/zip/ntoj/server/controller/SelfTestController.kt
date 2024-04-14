package zip.ntoj.server.controller

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import zip.ntoj.server.ext.success
import zip.ntoj.server.model.SelfTestSubmission
import zip.ntoj.server.service.LanguageService
import zip.ntoj.server.service.SelfTestSubmissionService
import zip.ntoj.server.service.UserService
import zip.ntoj.shared.model.JudgeStage
import zip.ntoj.shared.model.R
import zip.ntoj.shared.model.SubmissionStatus

@RestController
@RequestMapping("/self_test")
class SelfTestController(
    private val selfTestSubmissionService: SelfTestSubmissionService,
    private val languageService: LanguageService,
    private val userService: UserService,
) {
    @GetMapping("{id}")
    fun get(
        @PathVariable id: Long,
    ): ResponseEntity<R<SelfTestSubmissionDto>> {
        val selfTestSubmission = selfTestSubmissionService.get(id)
        return R.success(200, "获取成功", SelfTestSubmissionDto.from(selfTestSubmission))
    }

    @PostMapping
    @SaCheckLogin
    fun submit(
        @RequestBody selfTestSubmissionRequest: SelfTestSubmissionRequest,
    ): ResponseEntity<R<SelfTestSubmissionDto>> {
        val user = userService.getUserById(StpUtil.getLoginIdAsLong())
        val selfTestSubmission =
            SelfTestSubmission(
                language = languageService.get(selfTestSubmissionRequest.language),
                code = selfTestSubmissionRequest.code,
                input = selfTestSubmissionRequest.input,
                user = user,
                timeLimit = selfTestSubmissionRequest.timeLimit,
                memoryLimit = selfTestSubmissionRequest.memoryLimit,
                expectedOutput = selfTestSubmissionRequest.output,
            )
        selfTestSubmissionService.add(selfTestSubmission)
        return R.success(200, "获取成功", SelfTestSubmissionDto.from(selfTestSubmission))
    }

    data class SelfTestSubmissionRequest(
        val language: Long,
        val code: String,
        val input: String,
        val output: String?,
        val timeLimit: Int,
        val memoryLimit: Int,
    )

    data class SelfTestSubmissionDto(
        val id: Long,
        val status: SubmissionStatus,
        val stage: JudgeStage,
        val memory: Int?,
        val time: Int?,
        val compileLog: String?,
        val input: String,
        val output: String?,
        val expectedOutput: String?,
    ) {
        companion object {
            fun from(selfTestSubmission: SelfTestSubmission): SelfTestSubmissionDto {
                return SelfTestSubmissionDto(
                    id = selfTestSubmission.selfTestSubmissionId!!,
                    status = selfTestSubmission.status,
                    stage = selfTestSubmission.judgeStage,
                    memory = selfTestSubmission.memory,
                    time = selfTestSubmission.time,
                    compileLog = selfTestSubmission.compileLog,
                    input = selfTestSubmission.input,
                    output = selfTestSubmission.output,
                    expectedOutput = selfTestSubmission.expectedOutput,
                )
            }
        }
    }
}
