package com.github.ntoj.app.server.controller

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import com.github.ntoj.app.server.exception.AppException
import com.github.ntoj.app.server.ext.success
import com.github.ntoj.app.server.model.entities.SelfTestSubmission
import com.github.ntoj.app.server.service.LanguageService
import com.github.ntoj.app.server.service.SelfTestSubmissionService
import com.github.ntoj.app.server.service.UserService
import com.github.ntoj.app.shared.model.JudgeStage
import com.github.ntoj.app.shared.model.R
import com.github.ntoj.app.shared.model.SubmissionStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
        if (!languageService.exists(selfTestSubmissionRequest.lang)) {
            throw AppException("语言不存在", 400)
        }
        val selfTestSubmission =
            SelfTestSubmission(
                code = selfTestSubmissionRequest.code,
                input = selfTestSubmissionRequest.input,
                user = user,
                timeLimit = selfTestSubmissionRequest.timeLimit,
                memoryLimit = selfTestSubmissionRequest.memoryLimit,
                expectedOutput = selfTestSubmissionRequest.output,
                lang = selfTestSubmissionRequest.lang,
            )
        selfTestSubmissionService.add(selfTestSubmission)
        return R.success(200, "获取成功", SelfTestSubmissionDto.from(selfTestSubmission))
    }

    data class SelfTestSubmissionRequest(
        val language: Long,
        val lang: String,
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
