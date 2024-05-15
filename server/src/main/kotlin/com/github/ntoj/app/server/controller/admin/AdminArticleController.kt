package com.github.ntoj.app.server.controller.admin

import cn.dev33.satoken.annotation.SaCheckPermission
import com.github.ntoj.app.server.ext.success
import com.github.ntoj.app.server.model.L
import com.github.ntoj.app.server.model.dtos.admin.ArticleDto
import com.github.ntoj.app.server.service.ArticleService
import com.github.ntoj.app.server.service.UserService
import com.github.ntoj.app.shared.model.R
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/article")
class AdminArticleController(
    val userService: UserService,
    val articleService: ArticleService,
) {
    @GetMapping("{id}")
    fun get(
        @PathVariable id: Long,
    ): ResponseEntity<R<ArticleDto>> {
        val article = articleService.get(id)
        return R.success(
            200,
            "获取成功",
            ArticleDto.from(article),
        )
    }

    @GetMapping
    fun getMany(
        @RequestParam(required = false, defaultValue = "1") current: Int,
        @RequestParam(required = false, defaultValue = "20") pageSize: Int,
    ): ResponseEntity<R<L<ArticleDto>>> {
        val list = articleService.get(desc = true, page = current, pageSize = pageSize)
        val count = articleService.count()
        return R.success(
            200,
            "获取成功",
            L(
                total = count,
                page = current,
                list = list.map { ArticleDto.from(it) },
            ),
        )
    }

    @PatchMapping("/{id}")
    @SaCheckPermission(value = ["PERM_EDIT_ALL_ARTICLES"])
    fun update(
        @PathVariable id: Long,
        @RequestBody articleRequest: ArticleRequest,
    ): ResponseEntity<R<ArticleDto>> {
        var article = articleService.get(id)
        if (articleRequest.title != null) {
            require(articleRequest.title.isNotBlank()) { "文章标题不能为空" }
            article.title = articleRequest.title
        }
        if (articleRequest.content != null) {
            require(articleRequest.content.isNotBlank()) { "文章内容不能为空" }
            article.content = articleRequest.content
        }
        if (articleRequest.visible != null) {
            article.visible = articleRequest.visible
        }
        article = articleService.update(article)
        return R.success(200, "修改成功", ArticleDto.from(article))
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission(value = ["PERM_EDIT_ALL_ARTICLES"])
    fun remove(
        @PathVariable id: Long,
    ): ResponseEntity<R<Unit>> {
        articleService.remove(id)
        return R.success(200, "删除成功")
    }

    data class ArticleRequest(
        val title: String?,
        val content: String?,
        val visible: Boolean?,
    )
}
