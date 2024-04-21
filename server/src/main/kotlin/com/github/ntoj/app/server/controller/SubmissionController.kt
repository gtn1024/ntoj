package com.github.ntoj.app.server.controller

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.annotation.SaMode
import com.fasterxml.jackson.annotation.JsonFormat
import com.github.ntoj.app.server.ext.success
import com.github.ntoj.app.server.model.L
import com.github.ntoj.app.server.model.dtos.UserDto
import com.github.ntoj.app.server.model.entities.Language
import com.github.ntoj.app.server.model.entities.Problem
import com.github.ntoj.app.server.model.entities.Submission
import com.github.ntoj.app.server.service.ProblemService
import com.github.ntoj.app.server.service.SubmissionService
import com.github.ntoj.app.server.service.SubmissionService.SubmissionScope.PROBLEM
import com.github.ntoj.app.shared.model.JudgeStage
import com.github.ntoj.app.shared.model.R
import com.github.ntoj.app.shared.model.SubmissionStatus
import com.github.ntoj.app.shared.model.TestcaseJudgeResult
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/submission")
class SubmissionController(
    private val submissionService: SubmissionService,
    private val problemService: ProblemService,
) {
    @GetMapping("/list")
    fun index(
        @RequestParam(required = false, defaultValue = "1") current: Int,
        @RequestParam(required = false, defaultValue = "50") pageSize: Int,
    ): ResponseEntity<R<L<SubmissionListDto>>> {
        val list =
            submissionService.get(
                onlyVisibleProblem = true,
                page = current,
                pageSize = pageSize,
                desc = true,
                scope = PROBLEM,
            )
        val count = submissionService.count(true, PROBLEM)
        return R.success(200, "获取成功", L(count, current, list.map { SubmissionListDto.from(it) }))
    }

    data class SubmissionListDto(
        val id: Long,
        val status: SubmissionStatus,
        val time: Int? = null,
        val memory: Int? = null,
        val language: String? = null,
        val user: SubmissionUserDto,
        val problem: SubmissionProblemDto,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") val submitTime: Instant,
    ) {
        companion object {
            fun from(submission: Submission): SubmissionListDto {
                return SubmissionListDto(
                    id = submission.submissionId!!,
                    status = submission.status,
                    time = submission.time,
                    memory = submission.memory,
                    language = submission.language?.languageName,
                    user = SubmissionUserDto.from(submission),
                    problem = SubmissionProblemDto.from(submission),
                    submitTime = submission.createdAt!!,
                )
            }
        }

        data class SubmissionUserDto(
            val username: String,
        ) {
            companion object {
                fun from(submission: Submission): SubmissionUserDto {
                    return SubmissionUserDto(
                        username = submission.user?.username!!,
                    )
                }
            }
        }

        data class SubmissionProblemDto(
            val title: String,
            val alias: String,
        ) {
            companion object {
                fun from(submission: Submission): SubmissionProblemDto {
                    return SubmissionProblemDto(
                        title = submission.problem?.title!!,
                        alias = submission.problem?.alias!!,
                    )
                }
            }
        }
    }

    @GetMapping("/{id}")
    fun get(
        @PathVariable id: Long,
    ): ResponseEntity<R<SubmissionDto>> {
        val submission = submissionService.get(id)
        return R.success(200, "获取成功", SubmissionDto.from(submission))
    }

    @PostMapping("/{id}/rejudge")
    @SaCheckLogin
    @SaCheckRole(value = ["COACH", "ADMIN", "SUPER_ADMIN"], mode = SaMode.OR)
    fun rejudge(
        @PathVariable id: Long,
    ): ResponseEntity<R<Void>> {
        val submission = submissionService.get(id)
        val problem = problemService.get(submission.problem?.problemId!!)
        if (submission.status == SubmissionStatus.ACCEPTED) {
            problem.acceptedTimes--
            problemService.update(problem)
        }
        submission.judgeStage = JudgeStage.PENDING
        submission.status = SubmissionStatus.PENDING
        submissionService.update(submission)
        return R.success(200, "操作成功")
    }

    data class SubmissionDto(
        val id: Long,
        val user: UserDto,
        val code: String,
        val status: SubmissionStatus,
        val stage: JudgeStage,
        val memory: Int?,
        val time: Int?,
        val language: LanguageDto,
        val problem: ProblemDto,
        val compileLog: String?,
        val testcaseResult: List<TestcaseJudgeResult>?,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") val submitTime: Instant,
    ) {
        companion object {
            fun from(submission: Submission) =
                SubmissionDto(
                    id = submission.submissionId!!,
                    status = submission.status,
                    user = UserDto.from(submission.user!!),
                    stage = submission.judgeStage,
                    code = submission.code!!,
                    compileLog = submission.compileLog,
                    testcaseResult = submission.testcaseResult,
                    memory = submission.memory,
                    time = submission.time,
                    language = LanguageDto.from(submission.language!!),
                    submitTime = submission.createdAt!!,
                    problem = ProblemDto.from(submission.problem!!),
                )
        }

        data class ProblemDto(
            val title: String?,
            val alias: String?,
        ) {
            companion object {
                fun from(problem: Problem): ProblemDto =
                    ProblemDto(
                        title = problem.title,
                        alias = problem.alias,
                    )
            }
        }

        data class LanguageDto(
            val languageName: String,
        ) {
            companion object {
                fun from(language: Language): LanguageDto {
                    return LanguageDto(
                        languageName = language.languageName,
                    )
                }
            }
        }
    }
}
