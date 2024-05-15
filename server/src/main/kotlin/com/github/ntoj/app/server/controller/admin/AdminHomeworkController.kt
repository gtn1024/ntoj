package com.github.ntoj.app.server.controller.admin

import cn.dev33.satoken.annotation.SaCheckPermission
import cn.dev33.satoken.annotation.SaMode
import cn.dev33.satoken.stp.StpUtil
import com.github.ntoj.app.server.exception.AppException
import com.github.ntoj.app.server.ext.success
import com.github.ntoj.app.server.model.L
import com.github.ntoj.app.server.model.dtos.admin.HomeworkDto
import com.github.ntoj.app.server.model.entities.Homework
import com.github.ntoj.app.server.model.entities.Submission
import com.github.ntoj.app.server.model.entities.User
import com.github.ntoj.app.server.service.GroupService
import com.github.ntoj.app.server.service.HomeworkService
import com.github.ntoj.app.server.service.ProblemService
import com.github.ntoj.app.server.service.SubmissionService
import com.github.ntoj.app.server.service.UserService
import com.github.ntoj.app.shared.model.R
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/admin/homework")
class AdminHomeworkController(
    val homeworkService: HomeworkService,
    val problemService: ProblemService,
    val groupService: GroupService,
    val submissionService: SubmissionService,
    val userService: UserService,
) {
    @GetMapping("{id}")
    fun get(
        @PathVariable id: Long,
    ): ResponseEntity<R<HomeworkDto>> {
        val homework = homeworkService.get(id)
        return R.success(
            200,
            "获取成功",
            HomeworkDto.from(homework),
        )
    }

    @GetMapping
    fun get(
        @RequestParam(required = false, defaultValue = "1") current: Int,
        @RequestParam(required = false, defaultValue = "20") pageSize: Int,
    ): ResponseEntity<R<L<HomeworkDto>>> {
        val list = homeworkService.get(desc = true, page = current, pageSize = pageSize)
        val count = homeworkService.count()
        return R.success(
            200,
            "获取成功",
            L(
                total = count,
                page = current,
                list = list.map { HomeworkDto.from(it) },
            ),
        )
    }

    @PostMapping
    @SaCheckPermission(value = ["PERM_CREATE_HOMEWORK"])
    fun create(
        @RequestBody homeworkRequest: HomeworkRequest,
    ): ResponseEntity<R<HomeworkDto>> {
        val user = userService.getUserById(StpUtil.getLoginIdAsLong())
        var homework =
            Homework(
                homeworkRequest.title,
                Instant.ofEpochSecond(homeworkRequest.startTime),
                Instant.ofEpochSecond(homeworkRequest.endTime),
                homeworkRequest.groups.map {
                    groupService.get(it)
                },
                homeworkRequest.problems.map {
                    problemService.get(it)
                },
                user,
            )
        homework = homeworkService.create(homework)
        return R.success(200, "创建成功", HomeworkDto.from(homework))
    }

    @PatchMapping("{id}")
    @SaCheckPermission(value = ["PERM_EDIT_HOMEWORK", "PERM_EDIT_OWN_HOMEWORK"], mode = SaMode.OR)
    fun update(
        @PathVariable id: Long,
        @RequestBody homeworkRequest: HomeworkRequest,
    ): ResponseEntity<R<HomeworkDto>> {
        var homework = homeworkService.get(id)
        if (StpUtil.hasPermission("PERM_EDIT_HOMEWORK") && homework.author.userId != StpUtil.getLoginIdAsLong()) {
            throw AppException("无权限", 403)
        }
        homework.title = homeworkRequest.title
        homework.startTime = Instant.ofEpochSecond(homeworkRequest.startTime)
        homework.endTime = Instant.ofEpochSecond(homeworkRequest.endTime)
        homework.groups =
            homeworkRequest.groups.map {
                groupService.get(it)
            }
        homework.problems =
            homeworkRequest.problems.map {
                problemService.get(it)
            }
        homework = homeworkService.update(homework)
        return R.success(200, "修改成功", HomeworkDto.from(homework))
    }

    @GetMapping("{id}/export")
    fun export(
        @PathVariable id: Long,
    ): ResponseEntity<InputStreamResource> {
        val homework = homeworkService.get(id)
        val users = homework.groups.flatMap { it.users }.distinctBy { it.userId }
        val problems = homework.problems
        val sb = StringBuilder()
        sb.append("Username,RealName,Solved")
        for (i in problems.indices) {
            sb.append(",${problems[i].problemId}-${problems[i].alias}")
        }
        sb.append("\n")
        val list = mutableListOf<HomeworkUserData>()
        for (user in users) {
            val submissionData = mutableMapOf<Long, Submission>()
            for (i in problems.indices) {
                val submission = submissionService.getSolvedHomeworkProblemSubmission(user, problems[i], homework.endTime)
                if (submission != null) {
                    submissionData[problems[i].problemId!!] = submission
                }
                list.add(
                    HomeworkUserData(
                        user,
                        problems.count { submissionData[it.problemId!!] != null },
                        submissionData,
                    ),
                )
            }
        }
        list.sortByDescending { it.solved }
        for (userData in list) {
            sb.append("${userData.user.username},${userData.user.displayName},${userData.solved}")
            for (i in problems.indices) {
                sb.append(",")
                if (userData.submissionData[problems[i].problemId!!] != null) {
                    sb.append("ACCEPTED")
                }
            }
            sb.append("\n")
        }
        val filename = "homework_${homework.homeworkId}.csv"
        val csv = sb.toString()
        val file = InputStreamResource(csv.byteInputStream())
        return ResponseEntity.ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" +
                    filename,
            )
            .contentType(MediaType.parseMediaType("text/csv")).body(file)
    }

    data class HomeworkUserData(
        val user: User,
        var solved: Int,
        var submissionData: Map<Long, Submission>,
    )

    @DeleteMapping("/{id}")
    @SaCheckPermission(value = ["PERM_EDIT_HOMEWORK", "PERM_EDIT_OWN_HOMEWORK"], mode = SaMode.OR)
    fun remove(
        @PathVariable id: Long,
    ): ResponseEntity<R<Unit>> {
        val homework = homeworkService.get(id)
        if (StpUtil.hasPermission("PERM_EDIT_HOMEWORK") && homework.author.userId != StpUtil.getLoginIdAsLong()) {
            throw AppException("无权限", 403)
        }
        homeworkService.remove(id)
        return R.success(200, "删除成功")
    }

    data class HomeworkRequest(
        val title: String,
        val startTime: Long,
        val endTime: Long,
        val groups: List<Long>,
        val problems: List<Long>,
    )
}
