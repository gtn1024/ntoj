package com.github.ntoj.app.server.controller.admin

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.annotation.SaMode
import cn.dev33.satoken.stp.StpUtil
import com.fasterxml.jackson.annotation.JsonFormat
import com.github.ntoj.app.server.exception.AppException
import com.github.ntoj.app.server.ext.fail
import com.github.ntoj.app.server.ext.success
import com.github.ntoj.app.server.model.L
import com.github.ntoj.app.server.model.entities.Contest
import com.github.ntoj.app.server.model.entities.ContestProblem
import com.github.ntoj.app.server.model.entities.ContestUser
import com.github.ntoj.app.server.service.ContestService
import com.github.ntoj.app.server.service.UserService
import com.github.ntoj.app.shared.model.R
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
@RequestMapping("/admin/contest")
@SaCheckLogin
@SaCheckRole(value = ["COACH", "ADMIN", "SUPER_ADMIN"], mode = SaMode.OR)
class AdminContestController(
    private val userService: UserService,
    private val contestService: ContestService,
) {
    @GetMapping
    fun index(
        @RequestParam(required = false, defaultValue = "1") current: Int,
        @RequestParam(required = false, defaultValue = "20") pageSize: Int,
    ): ResponseEntity<R<L<*>>> {
        val list = contestService.get(desc = true, page = current, pageSize = pageSize)
        val count = contestService.count(false)
        return R.success(
            200,
            "获取成功",
            L(
                total = count,
                page = current,
                list = list.map { AdminContestDto.from(it) },
            ),
        )
    }

    @GetMapping("{id}")
    fun get(
        @PathVariable id: Long,
    ): ResponseEntity<R<AdminContestDto>> {
        val contest = contestService.get(id)
        return R.success(200, "获取成功", AdminContestDto.from(contest))
    }

    @DeleteMapping("{id}")
    fun delete(
        @PathVariable id: Long,
    ): ResponseEntity<R<Void>> {
        if (!contestService.exists(id)) return R.fail(404, "竞赛不存在")
        contestService.delete(id)
        return R.success(200, "删除成功")
    }

    @PostMapping
    fun add(
        @RequestBody request: ContestRequest,
    ): ResponseEntity<R<AdminContestDto>> {
        val author = userService.getUserById(StpUtil.getLoginIdAsLong())
        var contest =
            Contest(
                title = request.title,
                description = request.description,
                startTime = Instant.ofEpochSecond(request.startTime),
                endTime = Instant.ofEpochSecond(request.endTime),
                freezeTime = request.freezeTime,
                type = request.type,
                permission = request.permission,
                password = request.password,
                visible = request.visible,
                showFinalBoard = request.showFinalBoard,
                author = author,
                problems = request.problems,
                users =
                    request.users.map {
                        if (!userService.existsById(it)) throw AppException("用户不存在", 404)
                        ContestUser(userId = it, Instant.now().toEpochMilli())
                    }.toMutableList(),
            )
        contest = contestService.add(contest)
        return R.success(200, "添加成功", AdminContestDto.from(contest))
    }

    @PatchMapping("{id}")
    fun update(
        @RequestBody request: ContestRequest,
        @PathVariable id: Long,
    ): ResponseEntity<R<AdminContestDto>> {
        var contest = contestService.get(id)
        contest.title = request.title
        contest.description = request.description
        contest.startTime = Instant.ofEpochSecond(request.startTime)
        contest.endTime = Instant.ofEpochSecond(request.endTime)
        contest.freezeTime = request.freezeTime
        contest.type = request.type
        contest.permission = request.permission
        contest.password = request.password
        contest.problems = request.problems
        contest.users =
            request.users.map {
                if (!userService.existsById(it)) throw AppException("用户不存在", 404)
                ContestUser(userId = it, Instant.now().toEpochMilli())
            }.toMutableList()
        contest.visible = request.visible
        contest.showFinalBoard = request.showFinalBoard
        contest = contestService.update(contest)
        return R.success(200, "更新成功", AdminContestDto.from(contest))
    }

    data class ContestRequest(
        val title: String,
        val description: String?,
        val startTime: Long,
        val endTime: Long,
        val freezeTime: Int?,
        val type: Contest.ContestType = Contest.ContestType.ICPC,
        val permission: Contest.ContestPermission = Contest.ContestPermission.PUBLIC,
        val password: String?,
        val problems: List<ContestProblem> = listOf(),
        val users: List<Long> = listOf(),
        val visible: Boolean = false,
        val showFinalBoard: Boolean = false,
    )

    data class AdminContestDto(
        val id: Long,
        val title: String,
        val description: String?,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") val startTime: Instant,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") val endTime: Instant,
        val freezeTime: Int?,
        val type: Contest.ContestType,
        val permission: Contest.ContestPermission,
        val password: String?,
        val problems: List<ContestProblem> = listOf(),
        val users: List<Long> = listOf(),
        val visible: Boolean,
        val showFinalBoard: Boolean,
        val author: String,
    ) {
        companion object {
            fun from(contest: Contest) =
                AdminContestDto(
                    id = contest.contestId!!,
                    title = contest.title,
                    description = contest.description,
                    startTime = contest.startTime,
                    endTime = contest.endTime,
                    freezeTime = contest.freezeTime,
                    type = contest.type,
                    permission = contest.permission,
                    password = contest.password,
                    problems = contest.problems,
                    users = contest.users.map { it.userId },
                    visible = contest.visible,
                    showFinalBoard = contest.showFinalBoard,
                    author = contest.author.username,
                )
        }
    }
}
