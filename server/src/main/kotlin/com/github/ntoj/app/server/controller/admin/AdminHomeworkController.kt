package com.github.ntoj.app.server.controller.admin

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.annotation.SaMode
import com.github.ntoj.app.server.ext.success
import com.github.ntoj.app.server.model.L
import com.github.ntoj.app.server.model.dtos.admin.HomeworkDto
import com.github.ntoj.app.server.model.entities.Homework
import com.github.ntoj.app.server.service.GroupService
import com.github.ntoj.app.server.service.HomeworkService
import com.github.ntoj.app.server.service.ProblemService
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
@RequestMapping("/admin/homework")
@SaCheckLogin
@SaCheckRole(value = ["COACH", "ADMIN", "SUPER_ADMIN"], mode = SaMode.OR)
class AdminHomeworkController(
    val homeworkService: HomeworkService,
    val problemService: ProblemService,
    val groupService: GroupService,
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
    fun create(
        @RequestBody homeworkRequest: HomeworkRequest,
    ): ResponseEntity<R<HomeworkDto>> {
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
            )
        homework = homeworkService.create(homework)
        return R.success(200, "创建成功", HomeworkDto.from(homework))
    }

    @PatchMapping("{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody homeworkRequest: HomeworkRequest,
    ): ResponseEntity<R<HomeworkDto>> {
        var homework = homeworkService.get(id)
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

    @DeleteMapping("/{id}")
    fun remove(
        @PathVariable id: Long,
    ): ResponseEntity<R<Unit>> {
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
