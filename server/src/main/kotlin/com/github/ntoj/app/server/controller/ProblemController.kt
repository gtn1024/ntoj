package com.github.ntoj.app.server.controller

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import com.github.ntoj.app.server.exception.AppException
import com.github.ntoj.app.server.ext.success
import com.github.ntoj.app.server.model.L
import com.github.ntoj.app.server.model.entities.Problem
import com.github.ntoj.app.server.model.entities.ProblemSample
import com.github.ntoj.app.server.model.entities.Submission
import com.github.ntoj.app.server.service.LanguageService
import com.github.ntoj.app.server.service.ProblemService
import com.github.ntoj.app.server.service.SubmissionService
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/problem")
class ProblemController(
    val problemService: ProblemService,
    val userService: UserService,
    val submissionService: SubmissionService,
    val languageService: LanguageService,
) {
    @GetMapping
    fun index(
        @RequestParam(required = false, defaultValue = "1") current: Int,
        @RequestParam(required = false, defaultValue = "10") pageSize: Int,
    ): ResponseEntity<R<L<ProblemListDto>>> {
        val list =
            problemService.get(onlyVisible = true, page = current, pageSize = pageSize)
        val count = problemService.count(true)
        return R.success(200, "获取成功", L(count, current, list.map { ProblemListDto.from(it) }))
    }

    @GetMapping("/{alias}")
    fun get(
        @PathVariable alias: String,
    ): ResponseEntity<R<ProblemDto>> {
        val problem = problemService.get(alias)
        return R.success(200, "获取成功", ProblemDto.from(problem))
    }

    @PostMapping("/{alias}/submit")
    @SaCheckLogin
    fun submitCode(
        @PathVariable alias: String,
        @RequestBody problemSubmissionRequest: ProblemSubmissionRequest,
    ): ResponseEntity<R<SubmissionDto>> {
        val problem = problemService.get(alias)
        if (problemSubmissionRequest.code.length > problem.codeLength * 1024) {
            throw AppException("代码长度超过限制", 400)
        }
        problem.submitTimes = (problem.submitTimes + 1).coerceAtLeast(problem.acceptedTimes + 1)
        problemService.update(problem)
        val user = userService.getUserById(StpUtil.getLoginIdAsLong())
        if (!languageService.exists(problemSubmissionRequest.lang)) {
            throw AppException("语言不存在", 400)
        }
        var submission =
            Submission(
                user = user,
                problem = problem,
                origin = Submission.SubmissionOrigin.PROBLEM,
                code = problemSubmissionRequest.code,
                status = SubmissionStatus.PENDING,
                judgeStage = JudgeStage.PENDING,
                lang = problemSubmissionRequest.lang,
            )
        submission = submissionService.new(submission)
        return R.success(200, "提交成功", SubmissionDto.from(submission))
    }

    data class SubmissionDto(
        val id: Long,
        val status: SubmissionStatus,
        val stage: JudgeStage,
    ) {
        companion object {
            fun from(submission: Submission) =
                SubmissionDto(
                    id = submission.submissionId!!,
                    status = submission.status,
                    stage = submission.judgeStage,
                )
        }
    }

    data class ProblemDto(
        val id: Long,
        val title: String,
        val alias: String,
        val background: String?,
        val description: String?,
        val inputDescription: String?,
        val outputDescription: String?,
        val timeLimit: Int?,
        val memoryLimit: Int?,
        val judgeTimes: Int?,
        val samples: List<ProblemSample>,
        val note: String?,
        val author: String?,
        val codeLength: Int,
        val submitTimes: Long,
        val acceptedTimes: Long,
    ) {
        companion object {
            fun from(problem: Problem): ProblemDto =
                ProblemDto(
                    id = problem.problemId!!,
                    title = problem.title,
                    alias = problem.alias,
                    background = problem.background,
                    description = problem.description,
                    inputDescription = problem.inputDescription,
                    outputDescription = problem.outputDescription,
                    timeLimit = problem.timeLimit,
                    memoryLimit = problem.memoryLimit,
                    judgeTimes = problem.judgeTimes,
                    samples = problem.samples ?: listOf(),
                    note = problem.note,
                    author = problem.author?.username,
                    codeLength = problem.codeLength,
                    submitTimes = problem.submitTimes,
                    acceptedTimes = problem.acceptedTimes,
                )
        }
    }

    data class ProblemListDto(
        val id: Long,
        val title: String,
        val alias: String,
        val submitTimes: Long,
        val acceptedTimes: Long,
    ) {
        companion object {
            fun from(problem: Problem): ProblemListDto =
                ProblemListDto(
                    id = problem.problemId!!,
                    title = problem.title,
                    alias = problem.alias,
                    submitTimes = problem.submitTimes,
                    acceptedTimes = problem.acceptedTimes,
                )
        }
    }
}

data class ProblemSubmissionRequest(
    val code: String,
    val lang: String,
)
