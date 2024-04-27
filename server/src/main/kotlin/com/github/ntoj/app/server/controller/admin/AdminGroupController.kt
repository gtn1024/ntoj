package com.github.ntoj.app.server.controller.admin

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.annotation.SaMode
import com.github.ntoj.app.server.ext.success
import com.github.ntoj.app.server.model.L
import com.github.ntoj.app.server.model.dtos.admin.GroupDto
import com.github.ntoj.app.server.model.entities.Group
import com.github.ntoj.app.server.service.GroupService
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

@RestController
@RequestMapping("/admin/group")
@SaCheckLogin
@SaCheckRole(value = ["COACH", "ADMIN", "SUPER_ADMIN"], mode = SaMode.OR)
class AdminGroupController(
    private val groupService: GroupService,
    private val userService: UserService,
) {
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

    @GetMapping("search")
    fun search(
        @RequestParam keyword: String,
    ): ResponseEntity<R<List<GroupDto>>> {
        val groups = groupService.search(keyword)
        return R.success(200, "获取成功", groups.map { GroupDto.from(it) })
    }

    @PostMapping
    fun create(
        @RequestBody groupRequest: GroupRequest,
    ): ResponseEntity<R<GroupDto>> {
        require(groupRequest.name.isNotBlank()) { "组名不能为空" }
        var group =
            Group(
                name = groupRequest.name,
                users =
                    groupRequest.users.map { uid ->
                        userService.getUserById(uid)
                    },
            )
        group = groupService.create(group)
        return R.success(201, "创建成功", GroupDto.from(group))
    }

    @PatchMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody groupRequest: GroupRequest,
    ): ResponseEntity<R<GroupDto>> {
        require(groupRequest.name.isNotBlank()) { "组名不能为空" }
        var group = groupService.get(id)
        group.name = groupRequest.name
        group.users =
            groupRequest.users.map { uid ->
                userService.getUserById(uid)
            }
        group = groupService.update(group)
        return R.success(200, "更新成功", GroupDto.from(group))
    }

    @DeleteMapping("/{id}")
    fun remove(
        @PathVariable id: Long,
    ): ResponseEntity<R<Unit>> {
        groupService.remove(id)
        return R.success(200, "删除成功")
    }

    data class GroupRequest(
        val name: String,
        val users: List<Long>,
    )
}
