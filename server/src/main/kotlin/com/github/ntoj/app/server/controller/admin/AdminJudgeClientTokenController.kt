package com.github.ntoj.app.server.controller.admin

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.annotation.SaMode
import com.github.ntoj.app.server.ext.success
import com.github.ntoj.app.server.model.JudgeClientToken
import com.github.ntoj.app.server.model.L
import com.github.ntoj.app.server.service.JudgeClientTokenService
import com.github.ntoj.app.shared.model.R
import com.github.ntoj.app.shared.util.randomString
import jakarta.validation.Valid
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
@RequestMapping("/admin/judge_client_token")
@SaCheckLogin
@SaCheckRole(value = ["SUPER_ADMIN"], mode = SaMode.OR)
class AdminJudgeClientTokenController(
    private val judgeClientTokenService: JudgeClientTokenService,
) {
    @GetMapping
    fun index(
        @RequestParam(required = false, defaultValue = "1") current: Int,
        @RequestParam(required = false, defaultValue = "20") pageSize: Int,
    ): ResponseEntity<R<L<JudgeClientTokenDto>>> {
        val list = judgeClientTokenService.get(page = current, pageSize = pageSize)
        val count = judgeClientTokenService.count()
        return R.success(
            200,
            "获取成功",
            L(
                total = count,
                page = current,
                list = list.map { JudgeClientTokenDto.from(it) },
            ),
        )
    }

    @GetMapping("{id}")
    fun get(
        @PathVariable id: Long,
    ): ResponseEntity<R<JudgeClientTokenDto>> {
        val judgeClientToken = judgeClientTokenService.get(id)
        return R.success(
            200,
            "获取成功",
            JudgeClientTokenDto.from(judgeClientToken),
        )
    }

    @PostMapping
    fun create(
        @RequestBody @Valid
        judgeClientTokenRequest: JudgeClientTokenRequest,
    ): ResponseEntity<R<JudgeClientTokenDto>> {
        return R.success(
            200,
            "创建成功",
            JudgeClientTokenDto.from(
                judgeClientTokenService.new(
                    JudgeClientToken(
                        name = judgeClientTokenRequest.name,
                        token = randomString(),
                        enabled = judgeClientTokenRequest.enabled,
                    ),
                ),
            ),
        )
    }

    @PatchMapping("{id}")
    fun update(
        @RequestBody @Valid
        judgeClientTokenRequest: JudgeClientTokenRequest,
        @PathVariable id: Long,
    ): ResponseEntity<R<JudgeClientTokenDto>> {
        val judgeClientToken = judgeClientTokenService.get(id)
        if (judgeClientToken.name != judgeClientTokenRequest.name) {
            judgeClientToken.name = judgeClientTokenRequest.name
        }
        if (judgeClientToken.enabled != judgeClientTokenRequest.enabled) {
            judgeClientToken.enabled = judgeClientTokenRequest.enabled
        }
        return R.success(
            200,
            "修改成功",
            JudgeClientTokenDto.from(judgeClientTokenService.update(judgeClientToken)),
        )
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long,
    ): ResponseEntity<R<Unit>> {
        judgeClientTokenService.delete(id)
        return R.success(200, "删除成功")
    }
}

data class JudgeClientTokenDto(
    val id: Long,
    val name: String,
    val token: String,
    val enabled: Boolean,
) {
    companion object {
        fun from(judgeClientToken: JudgeClientToken): JudgeClientTokenDto {
            return JudgeClientTokenDto(
                id = judgeClientToken.tokenId!!,
                name = judgeClientToken.name,
                token = judgeClientToken.token,
                enabled = judgeClientToken.enabled,
            )
        }
    }
}

data class JudgeClientTokenRequest(
    var name: String,
    var enabled: Boolean,
)
