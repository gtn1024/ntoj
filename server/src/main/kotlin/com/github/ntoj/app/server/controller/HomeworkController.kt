package com.github.ntoj.app.server.controller

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import com.github.ntoj.app.server.exception.AppException
import com.github.ntoj.app.server.ext.success
import com.github.ntoj.app.server.model.dtos.HomeworkDto
import com.github.ntoj.app.server.model.entities.Group
import com.github.ntoj.app.server.model.entities.Homework
import com.github.ntoj.app.server.model.entities.User
import com.github.ntoj.app.server.service.HomeworkService
import com.github.ntoj.app.server.service.SubmissionService
import com.github.ntoj.app.server.service.UserService
import com.github.ntoj.app.shared.model.R
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/homework")
class HomeworkController(
    val homeworkService: HomeworkService,
    val userService: UserService,
    val submissionService: SubmissionService,
) {
    @SaCheckLogin
    @GetMapping("{id}")
    fun get(
        @PathVariable id: Long,
    ): ResponseEntity<R<HomeworkDto>> {
        val homework = homeworkService.get(id)
        val user = userService.getUserById(StpUtil.getLoginIdAsLong())
        if (!isUserInHomework(user, homework)) {
            throw AppException("该作业不属于你", 403)
        }
        return R.success(200, "获取成功", HomeworkDto.from(homework))
    }

    @SaCheckLogin
    @GetMapping("{id}/status")
    fun getStatus(
        @PathVariable id: Long,
    ): ResponseEntity<R<Map<Long, HomeworkProblemStatus>>> {
        val homework = homeworkService.get(id)
        val user = userService.getUserById(StpUtil.getLoginIdAsLong())
        if (!isUserInHomework(user, homework)) {
            throw AppException("该作业不属于你", 403)
        }

        val status: Map<Long, HomeworkProblemStatus> = getHomeworkStatus(homework, user)
        return R.success(200, "获取成功", status)
    }

    private fun getHomeworkStatus(
        homework: Homework,
        user: User,
    ): Map<Long, HomeworkProblemStatus> {
        val status: MutableMap<Long, HomeworkProblemStatus> = mutableMapOf()
        for (problem in homework.problems) {
            val submission = submissionService.getSolvedHomeworkProblemSubmission(user, problem, homework.endTime)
            status[problem.problemId!!] =
                HomeworkProblemStatus(
                    solved = submission != null,
                    submissionId = submission?.submissionId,
                )
        }
        return status
    }

    private fun isUserInHomework(
        user: User,
        homework: Homework,
    ): Boolean {
        for (group in homework.groups) {
            if (isUserInGroup(user, group)) {
                return true
            }
        }
        return false
    }

    private fun isUserInGroup(
        user: User,
        group: Group,
    ): Boolean {
        val groups = userService.getUserGroups(user)
        return groups.contains(group)
    }

    data class HomeworkProblemStatus(
        val solved: Boolean,
        val submissionId: Long?,
    )
}
