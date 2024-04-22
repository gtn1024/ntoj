package com.github.ntoj.app.server.controller.admin

import com.github.ntoj.app.server.ext.success
import com.github.ntoj.app.server.model.L
import com.github.ntoj.app.server.model.dtos.admin.GroupDto
import com.github.ntoj.app.server.service.GroupService
import com.github.ntoj.app.shared.model.R
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/group")
class AdminGroupController(private val groupService: GroupService) {
    @GetMapping("{id}")
    fun get(
        @PathVariable id: Long,
    ): ResponseEntity<R<GroupDto>> {
        val group = groupService.get(id)
        return R.success(
            200,
            "获取成功",
            GroupDto.from(group),
        )
    }

    @GetMapping
    fun getMany(
        @RequestParam(required = false, defaultValue = "1") current: Int,
        @RequestParam(required = false, defaultValue = "20") pageSize: Int,
    ): ResponseEntity<R<L<GroupDto>>> {
        val list = groupService.get(desc = true, page = current, pageSize = pageSize)
        val count = groupService.count()
        return R.success(
            200,
            "获取成功",
            L(
                total = count,
                page = current,
                list = list.map { GroupDto.from(it) },
            ),
        )
    }

    @DeleteMapping("/{id}")
    fun remove(
        @PathVariable id: Long,
    ): ResponseEntity<R<Unit>> {
        groupService.remove(id)
        return R.success(200, "删除成功")
    }
}
