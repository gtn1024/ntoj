package zip.ntoj.server.controller.admin

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.annotation.SaMode
import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import zip.ntoj.server.ext.fail
import zip.ntoj.server.ext.success
import zip.ntoj.server.model.Contest
import zip.ntoj.server.model.L
import zip.ntoj.server.service.ContestService
import zip.ntoj.server.service.UserService
import zip.ntoj.shared.model.R
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

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<R<Void>> {
        if (!contestService.exists(id)) return R.fail(404, "竞赛不存在")
        contestService.delete(id)
        return R.success(200, "删除成功")
    }

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
        val problems: List<Long> = listOf(),
        val users: List<Long> = listOf(),
        val languages: List<Long> = listOf(),
        val allowAllLanguages: Boolean,
        val visible: Boolean,
        val showFinalBoard: Boolean,
        val author: String,
    ) {
        companion object {
            fun from(contest: Contest) = AdminContestDto(
                id = contest.contestId!!,
                title = contest.title,
                description = contest.description,
                startTime = contest.startTime,
                endTime = contest.endTime,
                freezeTime = contest.freezeTime,
                type = contest.type,
                permission = contest.permission,
                password = contest.password,
                problems = contest.problems.map { it.problemId!! },
                users = contest.users.map { it.userId!! },
                languages = contest.languages.map { it.languageId!! },
                allowAllLanguages = contest.allowAllLanguages,
                visible = contest.visible,
                showFinalBoard = contest.showFinalBoard,
                author = contest.author.username!!
            )
        }
    }
}
