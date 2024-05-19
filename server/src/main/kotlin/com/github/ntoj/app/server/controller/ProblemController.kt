package com.github.ntoj.app.server.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import cn.dev33.satoken.annotation.SaMode
import cn.dev33.satoken.stp.StpUtil
import com.github.ntoj.app.server.exception.AppException
import com.github.ntoj.app.server.ext.success
import com.github.ntoj.app.server.model.L
import com.github.ntoj.app.server.model.dtos.RecordDto
import com.github.ntoj.app.server.model.entities.Problem
import com.github.ntoj.app.server.model.entities.ProblemSample
import com.github.ntoj.app.server.model.entities.Record
import com.github.ntoj.app.server.service.LanguageService
import com.github.ntoj.app.server.service.ProblemService
import com.github.ntoj.app.server.service.RecordService
import com.github.ntoj.app.server.service.UserService
import com.github.ntoj.app.shared.model.R
import com.github.ntoj.app.shared.model.RecordOrigin
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
@SaCheckPermission(value = ["PERM_VIEW"])
class ProblemController(
    val problemService: ProblemService,
    val userService: UserService,
    val languageService: LanguageService,
    private val recordService: RecordService,
) {
    @GetMapping
    fun index(
        @RequestParam(required = false, defaultValue = "1") current: Int,
        @RequestParam(required = false, defaultValue = "10") pageSize: Int,
    ): ResponseEntity<R<L<ProblemListDto>>> {
        val onlyVisible = !StpUtil.hasPermission("PERM_VIEW_HIDDEN_PROBLEMS")
        val list = problemService.get(onlyVisible, page = current, pageSize = pageSize)
        val count = problemService.count(onlyVisible)
        return R.success(200, "获取成功", L(count, current, list.map { ProblemListDto.from(it) }))
    }

    @GetMapping("/{alias}")
    @SaCheckPermission(value = ["PERM_VIEW_PROBLEMS", "PERM_VIEW_HIDDEN_PROBLEMS"], mode = SaMode.OR)
    fun get(
        @PathVariable alias: String,
    ): ResponseEntity<R<ProblemDto>> {
        val problem = problemService.get(alias)
        if (!problem.visible && StpUtil.hasPermission("PERM_VIEW_HIDDEN_PROBLEMS")) {
            throw AppException("题目不存在", 404)
        }
        return R.success(200, "获取成功", ProblemDto.from(problem))
    }

    @PostMapping("/{alias}/submit")
    @SaCheckPermission(value = ["PERM_SUBMIT_PROBLEM"])
    fun submitCode(
        @PathVariable alias: String,
        @RequestBody problemSubmissionRequest: ProblemSubmissionRequest,
    ): ResponseEntity<R<RecordDto>> {
        val (code, lang, input, selfTest) = problemSubmissionRequest
        require(languageService.exists(lang)) { "语言不存在" }
        val user = userService.getUserById(StpUtil.getLoginIdAsLong())
        val problem = problemService.get(alias)
        require(code.length <= problem.codeLength * 1024) { "代码长度超过限制" }
        if (selfTest) {
            require(!input.isNullOrBlank()) { "自测输入不能为空" }
        }
        val record =
            Record(
                user,
                problem,
                RecordOrigin.PROBLEM,
                contest = null,
                problemSubmissionRequest.lang,
                selfTestInput = null,
                problemSubmissionRequest.code,
            )
        if (selfTest) {
            record.origin = RecordOrigin.SELF_TEST
            record.selfTestInput = input
        } else {
            problem.submitTimes = (problem.submitTimes + 1).coerceAtLeast(problem.acceptedTimes + 1)
            problemService.update(problem)
        }
        recordService.create(record)
        return R.success(200, "提交成功", RecordDto.from(record))
    }

    data class ProblemDto(
        val id: Long,
        val title: String,
        val alias: String,
        val background: String?,
        val description: String?,
        val inputDescription: String?,
        val outputDescription: String?,
        val timeLimit: Int,
        val memoryLimit: Int,
        val judgeTimes: Int?,
        val samples: List<ProblemSample>,
        val note: String?,
        val author: String,
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
                    samples = problem.samples,
                    note = problem.note,
                    author = problem.author.username,
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
    val input: String?,
    val selfTest: Boolean = false,
)
