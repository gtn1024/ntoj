package zip.ntoj.server.controller

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import zip.ntoj.server.exception.AppException
import zip.ntoj.server.ext.success
import zip.ntoj.server.model.L
import zip.ntoj.server.model.Problem
import zip.ntoj.server.model.ProblemSample
import zip.ntoj.server.model.Submission
import zip.ntoj.server.service.LanguageService
import zip.ntoj.server.service.ProblemService
import zip.ntoj.server.service.SubmissionService
import zip.ntoj.server.service.UserService
import zip.ntoj.shared.model.JudgeStage
import zip.ntoj.shared.model.R
import zip.ntoj.shared.model.SubmissionStatus

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
    fun get(@PathVariable alias: String): ResponseEntity<R<ProblemDto>> {
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
        if (!problem.allowAllLanguages && problem.languages.none { it.languageId == problemSubmissionRequest.language }) {
            throw AppException("不支持的语言", 400)
        }
        if (problemSubmissionRequest.code.length > problem.codeLength * 1024) {
            throw AppException("代码长度超过限制", 400)
        }
        problem.submitTimes++
        problemService.update(problem)
        val language = languageService.get(problemSubmissionRequest.language)
        val user = userService.getUserById(StpUtil.getLoginIdAsLong())
        var submission = Submission(
            user = user,
            problem = problem,
            origin = Submission.SubmissionOrigin.PROBLEM,
            language = language,
            code = problemSubmissionRequest.code,
            status = SubmissionStatus.PENDING,
            judgeStage = JudgeStage.PENDING,
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
            fun from(submission: Submission) = SubmissionDto(
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
        val languages: List<Long> = listOf(),
        val allowAllLanguages: Boolean,
        val codeLength: Int,
        val submitTimes: Long,
        val acceptedTimes: Long,
    ) {
        companion object {
            fun from(problem: Problem): ProblemDto = ProblemDto(
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
                languages = problem.languages.map { it.languageId!! },
                allowAllLanguages = problem.allowAllLanguages,
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
            fun from(problem: Problem): ProblemListDto = ProblemListDto(
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
    val language: Long,
)
