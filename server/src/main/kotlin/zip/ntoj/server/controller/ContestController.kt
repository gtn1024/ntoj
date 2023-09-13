package zip.ntoj.server.controller

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import zip.ntoj.server.exception.AppException
import zip.ntoj.server.ext.success
import zip.ntoj.server.model.*
import zip.ntoj.server.service.*
import zip.ntoj.shared.model.JudgeStage
import zip.ntoj.shared.model.R
import zip.ntoj.shared.model.SubmissionStatus
import java.time.Instant

@RestController
@RequestMapping("/contest")
class ContestController(
    private val contestService: ContestService,
    private val problemService: ProblemService,
    private val languageService: LanguageService,
    private val userService: UserService,
    private val submissionService: SubmissionService,
) {
    @GetMapping
    fun index(
        @RequestParam(required = false, defaultValue = "1") current: Int,
        @RequestParam(required = false, defaultValue = "20") pageSize: Int,
    ): ResponseEntity<R<L<ContestDto>>> {
        val list = contestService.get(page = current, pageSize = pageSize)
        val count = contestService.count()
        return R.success(
            200,
            "获取成功",
            L(
                total = count,
                page = current,
                list = list.map { ContestDto.from(it) },
            ),
        )
    }

    @GetMapping("{id}")
    fun get(@PathVariable id: Long): ResponseEntity<R<ContestDto>> {
        val contest = contestService.get(id)
        return R.success(
            200,
            "获取成功",
            ContestDto.from(contest),
        )
    }

    @GetMapping("{id}/problems")
    fun getProblems(@PathVariable id: Long): ResponseEntity<R<List<ContestProblemDto>>> {
        val contest = contestService.get(id)
        val problems = contest.problems
        return R.success(
            200,
            "获取成功",
            problems.map {
                val problem = problemService.get(it.problemId)
                ContestProblemDto.from(it, problem)
            },
        )
    }

    @GetMapping("{id}/problem/{alias}")
    fun getProblem(@PathVariable id: Long, @PathVariable alias: String): ResponseEntity<R<Problem>> {
        val contest = contestService.get(id)
        val problem = contest.problems.find { it.contestProblemIndex == alphabetToNumber(alias) }?.let {
            problemService.get(it.problemId)
        }
        return R.success(
            200,
            "获取成功",
            problem,
        )
    }

    @PostMapping("{id}/problem/{alias}/submit")
    @SaCheckLogin
    fun submit(
        @PathVariable id: Long,
        @PathVariable alias: String,
        @RequestBody problemSubmissionRequest: ProblemSubmissionRequest,
    ): ResponseEntity<R<ProblemController.SubmissionDto>> {
        val contest = contestService.get(id)
        if (!contest.allowAllLanguages && contest.languages.none { it.languageId == problemSubmissionRequest.language }) {
            throw AppException("不支持的语言", 400)
        }
        val problem = contest.problems.find { it.contestProblemIndex == alphabetToNumber(alias) }?.let {
            problemService.get(it.problemId)
        } ?: throw AppException("题目不存在", 404)
        if (problemSubmissionRequest.code.length > problem.codeLength * 1024) {
            throw AppException("代码长度超过限制", 400)
        }
        val language = languageService.get(problemSubmissionRequest.language)
        val user = userService.getUserById(StpUtil.getLoginIdAsLong())
        var submission = Submission(
            user = user,
            problem = problem,
            origin = Submission.SubmissionOrigin.CONTEST,
            contestId = id,
            language = language,
            code = problemSubmissionRequest.code,
            status = SubmissionStatus.PENDING,
            judgeStage = JudgeStage.PENDING,
        )
        submission = submissionService
            .new(submission)
        return R.success(200, "提交成功", ProblemController.SubmissionDto.from(submission))
    }

    data class ContestDto(
        val id: Long,
        val title: String,
        val description: String?,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") val startTime: Instant,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") val endTime: Instant,
        val type: Contest.ContestType,
        val permission: Contest.ContestPermission,
        val userCount: Int,
        val author: String,
        val languages: List<Long> = listOf(),
        val allowAllLanguages: Boolean,
    ) {
        companion object {
            fun from(contest: Contest) = ContestDto(
                id = contest.contestId!!,
                title = contest.title,
                description = contest.description,
                startTime = contest.startTime,
                endTime = contest.endTime,
                type = contest.type,
                permission = contest.permission,
                userCount = contest.users.size,
                author = contest.author.username,
                languages = contest.languages.map { it.languageId!! },
                allowAllLanguages = contest.allowAllLanguages,
            )
        }
    }

    companion object {
        fun numberToAlphabet(num: Int): String {
            var res = ""
            var n = num
            while (n > 0) {
                val digit = n % 26
                res += ('A'.code + digit - 1).toChar()
                n /= 26
            }
            return res.reversed()
        }

        fun alphabetToNumber(str: String): Int {
            var res = 0
            for (c in str) {
                res *= 26
                res += c.code - 'A'.code + 1
            }
            return res
        }
    }

    data class ContestProblemDto(
        val alias: String,
        val title: String,
    ) {
        companion object {
            fun from(contestProblem: ContestProblem, problem: Problem) = ContestProblemDto(
                title = problem.title,
                alias = numberToAlphabet(contestProblem.contestProblemIndex),
            )
        }
    }
}
