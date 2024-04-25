package com.github.ntoj.app.server.controller.admin

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.annotation.SaMode
import com.github.ntoj.app.server.ext.success
import com.github.ntoj.app.server.model.L
import com.github.ntoj.app.server.model.dtos.admin.HomeworkDto
import com.github.ntoj.app.server.service.HomeworkService
import com.github.ntoj.app.shared.model.R
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/homework")
@SaCheckLogin
@SaCheckRole(value = ["COACH", "ADMIN", "SUPER_ADMIN"], mode = SaMode.OR)
class AdminHomeworkController(
    val homeworkService: HomeworkService,
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

    @DeleteMapping("/{id}")
    fun remove(
        @PathVariable id: Long,
    ): ResponseEntity<R<Unit>> {
        homeworkService.remove(id)
        return R.success(200, "删除成功")
    }
}
