package zip.ntoj.server.controller

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.cache.annotation.Cacheable
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
    private val contestClarificationService: ContestClarificationService,
) {
    @GetMapping
    fun index(
        @RequestParam(required = false, defaultValue = "1") current: Int,
        @RequestParam(required = false, defaultValue = "20") pageSize: Int,
    ): ResponseEntity<R<L<ContestDto>>> {
        val list = contestService.get(desc = true, page = current, pageSize = pageSize)
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
        if (StpUtil.isLogin()) {
            val user = userService.getUserById(StpUtil.getLoginIdAsLong())
            val contest = contestService.get(id)
            val hasPermission = contest.users.any { it.userId == user.userId }
            return R.success(
                200,
                "获取成功",
                ContestDto.from(contest, hasPermission),
            )
        }
        val contest = contestService.get(id)
        return R.success(
            200,
            "获取成功",
            ContestDto.from(contest),
        )
    }

    @PostMapping("{id}/register")
    @SaCheckLogin
    fun contestRegister(
        @PathVariable id: Long,
        @RequestBody contestRegisterRequest: ContestRegisterRequest,
    ): ResponseEntity<R<Void>> {
        val user = userService.getUserById(StpUtil.getLoginIdAsLong())
        val contest = contestService.get(id)
        if (contest.users.any { it.userId == user.userId }) {
            throw AppException("您已经报名了该比赛", 400)
        }
        if (contest.permission === Contest.ContestPermission.PRIVATE) {
            throw AppException("该比赛不允许报名", 400)
        }
        if (contest.permission === Contest.ContestPermission.PASSWORD &&
            contestRegisterRequest.password != contest.password
        ) {
            throw AppException("密码错误", 400)
        }
        contest.users.add(user)
        contestService.update(contest)
        return R.success(200, "报名成功")
    }

    data class ContestRegisterRequest(
        val password: String?,
    )

    @GetMapping("{id}/problemsStatistics")
    @Cacheable("contestStatistic", key = "#root.methodName +'_tk_'+ #id")
    fun getProblemsStatistics(@PathVariable id: Long): ResponseEntity<R<Map<String, ContestProblemStatisticsDto>>> {
        val contest = contestService.get(id)
        val problems = contest.problems
        val submissions = submissionService.getByContestId(id).filter {
            it.createdAt!! >= contest.startTime && it.createdAt!! <= contest.endTime
        }.filter {
            it.status != SubmissionStatus.COMPILE_ERROR
        }
        val mp: MutableMap<Pair<String, String>, Boolean> = mutableMapOf()
        return R.success(
            200,
            "获取成功",
            problems.associate { contestProblem ->
                val problem = problemService.get(contestProblem.problemId)
                var submitTimes: Long = 0
                var acceptedTimes: Long = 0
                submissions.filter { it.problem?.problemId == contestProblem.problemId }.forEach {
                    submitTimes++
                    if ((it.status == SubmissionStatus.ACCEPTED) &&
                        mp.contains(Pair(it.user?.username!!, problem.problemId.toString())).not()
                    ) {
                        acceptedTimes++
                        mp[Pair(it.user?.username!!, problem.problemId.toString())] = true
                    }
                }
                numberToAlphabet(contestProblem.contestProblemIndex) to ContestProblemStatisticsDto(
                    submitTimes = submitTimes,
                    acceptedTimes = acceptedTimes,
                )
            },
        )
    }

    @GetMapping("{id}/standing")
    @Cacheable("contestStanding", key = "#root.methodName +'_tk_'+ #id")
    fun getStanding(@PathVariable id: Long): ResponseEntity<R<List<ContestStandingSubmissionDto>>> {
        val contest = contestService.get(id)
        val problems = contest.problems
        val submissions = submissionService.getByContestId(id)
            .reversed()
            .filter {
                it.createdAt!! >= contest.startTime && it.createdAt!! <= contest.endTime
            }
        return R.success(
            200,
            "获取成功",
            submissions.map {
                ContestStandingSubmissionDto.from(
                    it,
                    problems.find { problem -> problem.problemId == it.problem?.problemId }
                        ?.let { problem -> numberToAlphabet(problem.contestProblemIndex) }!!,
                )
            },
        )
    }

    data class ContestStandingSubmissionDto(
        val id: Long,
        val user: String,
        val alias: String,
        val result: SubmissionStatus,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") val submitTime: Instant,
    ) {
        companion object {
            fun from(submission: Submission, alias: String) = ContestStandingSubmissionDto(
                id = submission.submissionId!!,
                user = submission.user?.username!!,
                alias = alias,
                result = submission.status,
                submitTime = submission.createdAt!!,
            )
        }
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

    @GetMapping("{id}/submission")
    @SaCheckLogin
    fun getSubmissions(@PathVariable id: Long): ResponseEntity<R<L<ContestSubmissionDto>>> {
        fun hasAdminPermission(role: UserRole): Boolean {
            return role.ordinal >= 2
        }

        val user = userService.getUserById(StpUtil.getLoginIdAsLong())
        val contest = contestService.get(id)
        val submissions = submissionService.getByContestId(id)
            .reversed()
            .filter {
                if (Instant.now() >= contest.endTime) {
                    true
                } else if (hasAdminPermission(user.role)) {
                    true
                } else {
                    it.user?.userId == user.userId
                }
            }
        return R.success(
            200,
            "获取成功",
            L(
                total = submissions.size.toLong(),
                page = 1,
                list = submissions.map {
                    val alias = contest.problems.find { problem -> problem.problemId == it.problem?.problemId }
                        ?.let { problem -> numberToAlphabet(problem.contestProblemIndex) }
                    ContestSubmissionDto.from(it, alias!!)
                },
            ),
        )
    }

    data class ContestSubmissionDto(
        val id: Long,
        val user: UserDto,
        val alias: String,
        val result: SubmissionStatus,
        val time: Int?,
        val memory: Int?,
        val language: String,
        val codeLength: Int,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") val submitTime: Instant,
    ) {
        companion object {
            fun from(submission: Submission, alias: String) = ContestSubmissionDto(
                id = submission.submissionId!!,
                user = UserDto.from(submission.user!!),
                alias = alias,
                result = submission.status,
                time = submission.time,
                memory = submission.memory,
                language = submission.language?.languageName!!,
                codeLength = submission.code?.length!!,
                submitTime = submission.createdAt!!,
            )
        }

        data class UserDto(
            val username: String,
            val realName: String?,
        ) {
            companion object {
                fun from(user: User) = UserDto(
                    username = user.username,
                    realName = user.realName,
                )
            }
        }
    }

    @GetMapping("{id}/clarifications")
    fun getClarifications(@PathVariable id: Long): ResponseEntity<R<List<ContestClarificationDto>>> {
        val clarifications = contestClarificationService.getByContestId(id).reversed()
        return R.success(
            200,
            "获取成功",
            clarifications.map { ContestClarificationDto.from(it) },
        )
    }

    @PostMapping("{id}/clarification")
    @SaCheckLogin
    fun newClarification(
        @PathVariable id: Long,
        @RequestBody contestClarificationRequest: ContestClarificationRequest,
    ): ResponseEntity<R<ContestClarificationDto>> {
        val contest = contestService.get(id)
        val user = userService.getUserById(StpUtil.getLoginIdAsLong())
        var clarification = ContestClarification(
            title = contestClarificationRequest.title,
            content = contestClarificationRequest.content,
            contestProblemId = contestClarificationRequest.contestProblemId?.let { alphabetToNumber(it) },
            user = user,
            contest = contest,
        )
        clarification = contestClarificationService.add(clarification)
        return R.success(200, "提交成功", ContestClarificationDto.from(clarification))
    }

    @GetMapping("{id}/clarification/{clarificationId}")
    fun getClarification(
        @PathVariable id: Long,
        @PathVariable clarificationId: Long,
    ): ResponseEntity<R<ContestClarificationDetailDto>> {
        val contest = contestService.get(id)
        val clarification = contestClarificationService.get(clarificationId)
        if (clarification.contest.contestId != contest.contestId) {
            throw AppException("该问题不属于该比赛", 400)
        }
        return R.success(200, "获取成功", ContestClarificationDetailDto.from(clarification))
    }

    @PostMapping("{id}/clarification/{clarificationId}/reply")
    @SaCheckLogin
    fun replyClarification(
        @PathVariable id: Long,
        @PathVariable clarificationId: Long,
        @RequestBody contestClarificationRequest: ContestClarificationReplyRequest,
    ): ResponseEntity<R<Void>> {
        val contest = contestService.get(id)
        val clarification = contestClarificationService.get(clarificationId)
        if (clarification.contest.contestId != contest.contestId) {
            throw AppException("该问题不属于该比赛", 400)
        }
        val user = userService.getUserById(StpUtil.getLoginIdAsLong())
        val response = ContestClarificationResponse(
            content = contestClarificationRequest.content,
            user = user,
        )
        clarification.responses.add(response)
        contestClarificationService.update(clarification)
        return R.success(200, "提交成功")
    }

    data class ContestProblemStatisticsDto(
        val submitTimes: Long,
        val acceptedTimes: Long,
    )

    data class ContestClarificationRequest(
        val title: String,
        val content: String,
        val contestProblemId: String? = null,
    )

    data class ContestClarificationReplyRequest(
        val content: String,
    )

    data class ContestClarificationDetailDto(
        val id: Long,
        val title: String,
        val user: String,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") val createdAt: Instant,
        val contestProblemAlias: String?,
        val content: String,
        val replies: List<ClarificationResponseDto>,
    ) {
        companion object {
            fun from(clarification: ContestClarification) = ContestClarificationDetailDto(
                id = clarification.clarificationId!!,
                title = clarification.title,
                user = clarification.user.username,
                createdAt = clarification.createdAt!!,
                contestProblemAlias = clarification.contestProblemId?.let { numberToAlphabet(it) },
                content = clarification.content,
                replies = clarification.responses.map { ClarificationResponseDto.from(it) },
            )
        }

        data class ClarificationResponseDto(
            val id: Long,
            val content: String,
            val user: String,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") val createdAt: Instant,
        ) {
            companion object {
                fun from(clarificationResponse: ContestClarificationResponse) = ClarificationResponseDto(
                    id = clarificationResponse.responseId!!,
                    content = clarificationResponse.content,
                    user = clarificationResponse.user.username,
                    createdAt = clarificationResponse.createdAt!!,
                )
            }
        }
    }

    data class ContestClarificationDto(
        val id: Long,
        val title: String,
        val user: String,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") val createdAt: Instant,
        val sticky: Boolean,
        val replyCount: Int,
        val contestProblemAlias: String?,
    ) {
        companion object {
            fun from(contestClarification: ContestClarification) = ContestClarificationDto(
                id = contestClarification.clarificationId!!,
                title = contestClarification.title,
                user = contestClarification.user.username,
                createdAt = contestClarification.createdAt!!,
                sticky = contestClarification.sticky,
                replyCount = contestClarification.responses.size,
                contestProblemAlias = contestClarification.contestProblemId?.let { numberToAlphabet(it) },
            )
        }
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
        val users: List<String>,
        val author: String,
        val languages: List<Long> = listOf(),
        val allowAllLanguages: Boolean,
        val hasPermission: Boolean,
        val freezeTime: Int?,
        val showFinalBoard: Boolean,
    ) {
        companion object {
            fun from(contest: Contest, hasPermission: Boolean = false) = ContestDto(
                id = contest.contestId!!,
                title = contest.title,
                description = contest.description,
                startTime = contest.startTime,
                endTime = contest.endTime,
                type = contest.type,
                permission = contest.permission,
                userCount = contest.users.size,
                users = contest.users.map { it.username },
                author = contest.author.username,
                languages = contest.languages.map { it.languageId!! },
                allowAllLanguages = contest.allowAllLanguages,
                hasPermission = hasPermission,
                freezeTime = contest.freezeTime,
                showFinalBoard = contest.showFinalBoard,
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
