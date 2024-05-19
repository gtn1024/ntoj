package com.github.ntoj.app.server.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import cn.dev33.satoken.annotation.SaMode
import com.github.ntoj.app.server.ext.fail
import com.github.ntoj.app.server.ext.success
import com.github.ntoj.app.server.model.L
import com.github.ntoj.app.server.model.dtos.RecordDto
import com.github.ntoj.app.server.service.RecordService
import com.github.ntoj.app.shared.model.R
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/record")
@SaCheckPermission(value = ["PERM_VIEW"])
class RecordController(
    private val recordService: RecordService,
) {
    @GetMapping
    fun index(
        @RequestParam(required = false, defaultValue = "1") current: Int,
        @RequestParam(required = false, defaultValue = "50") pageSize: Int,
    ): ResponseEntity<R<L<RecordDto>>> {
        val list =
            recordService.list(
                current,
                pageSize,
                desc = true,
            )
        val count = recordService.count()
        return R.success(200, "获取成功", L(count, current, list.map { RecordDto.from(it) }))
    }

    @GetMapping("{id}")
    fun get(
        @PathVariable id: String,
    ): ResponseEntity<out R<out Any>> {
        val record = recordService.get(id) ?: return R.fail(404, "记录不存在")
        return R.success(200, "获取成功", RecordDto.from(record))
    }

    @PostMapping("/{id}/rejudge")
    @SaCheckPermission(value = ["PERM_REJUDGE_RECORD"], mode = SaMode.OR)
    fun rejudge(
        @PathVariable id: String,
    ): ResponseEntity<R<Void>> {
        recordService.rejudge(id)
        return R.success(200, "操作成功")
    }
}
