package zip.ntoj.server.controller

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import zip.ntoj.server.ext.success
import zip.ntoj.server.model.L
import zip.ntoj.server.model.Submission
import zip.ntoj.server.service.SubmissionService
import zip.ntoj.shared.model.R
import zip.ntoj.shared.model.SubmissionStatus
import java.time.Instant

@RestController
@RequestMapping("/submission")
class SubmissionController(
    private val submissionService: SubmissionService,
) {
    @GetMapping("/list")
    fun index(
        @RequestParam(required = false, defaultValue = "1") current: Int,
        @RequestParam(required = false, defaultValue = "50") pageSize: Int,
    ): ResponseEntity<R<L<SubmissionListDto>>> {
        val list =
            submissionService.get(onlyVisibleProblem = true, page = current, pageSize = pageSize, desc = true)
        val count = submissionService.count(true)
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
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC") val submitTime: Instant,
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
    fun get(@PathVariable id: Long): ResponseEntity<R<SubmissionDto>> {
        val submission = submissionService.get(id)
        return R.success(200, "获取成功", SubmissionDto.from(submission))
    }
}
