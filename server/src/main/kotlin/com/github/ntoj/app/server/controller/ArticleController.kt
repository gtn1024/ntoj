package com.github.ntoj.app.server.controller

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import com.github.ntoj.app.server.ext.success
import com.github.ntoj.app.server.model.L
import com.github.ntoj.app.server.model.dtos.UserDto
import com.github.ntoj.app.server.model.entities.Article
import com.github.ntoj.app.server.service.ArticleService
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
@RequestMapping("/article")
class ArticleController(
    val articlesService: ArticleService,
    val userService: UserService,
) {
    @GetMapping
    fun getMany(
        @RequestParam(required = false, defaultValue = "1") current: Int,
        @RequestParam(required = false, defaultValue = "10") pageSize: Int,
        @RequestParam(required = false) problemAlias: String?,
    ): ResponseEntity<R<L<ArticleDto>>> {
        val list =
            articlesService.get(
                onlyVisible = true,
                desc = true,
                page = current,
                pageSize = pageSize,
                problemAlias = problemAlias,
            )
        val count = articlesService.count(true, problemAlias)
        return R.success(200, "获取成功", L(count, current, list.map { ArticleDto.from(it) }))
    }

    @GetMapping("/{id}")
    fun getOne(
        @PathVariable id: Long,
    ): ResponseEntity<R<ArticleDto>> {
        return R.success(200, "获取成功", ArticleDto.from(articlesService.get(id)))
    }

    @SaCheckLogin
    @PostMapping
    fun create(
        @RequestBody articleRequest: ArticleRequest,
    ): ResponseEntity<R<ArticleDto>> {
        require(!articleRequest.title.isNullOrBlank()) { "文章标题不能为空" }
        require(!articleRequest.content.isNullOrBlank()) { "文章内容不能为空" }

        val user = userService.getUserById(StpUtil.getLoginIdAsLong())

        var article =
            Article(
                title = articleRequest.title,
                content = articleRequest.content,
                author = user,
            )
        if (articleRequest.problemAlias != "") {
            article.problemAlias = articleRequest.problemAlias
        }

        article = articlesService.create(article)
        return R.success(200, "创建成功", ArticleDto.from(article))
    }

    @SaCheckLogin
    @PatchMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody articleRequest: ArticleRequest,
    ): ResponseEntity<R<ArticleDto>> {
        var article = articlesService.get(id)
        val user = userService.getUserById(StpUtil.getLoginIdAsLong())
        require(user.userId == article.author.userId) { "未授权" }
        if (articleRequest.title != null) {
            require(articleRequest.title.isNotBlank()) { "文章标题不能为空" }
            article.title = articleRequest.title
        }
        if (articleRequest.content != null) {
            require(articleRequest.content.isNotBlank()) { "文章内容不能为空" }
            article.content = articleRequest.content
        }
        article = articlesService.update(article)
        return R.success(200, "修改成功", ArticleDto.from(article))
    }

    @SaCheckLogin
    @DeleteMapping("/{id}")
    fun remove(
        @PathVariable id: Long,
    ): ResponseEntity<R<Unit>> {
        val article = articlesService.get(id)
        val user = userService.getUserById(StpUtil.getLoginIdAsLong())
        require(user.userId == article.author.userId) { "未授权" }
        articlesService.remove(id)
        return R.success(200, "删除成功")
    }
}

data class ArticleRequest(
    val title: String?,
    val content: String?,
    val problemAlias: String,
)

data class ArticleDto(
    val id: Long,
    val title: String,
    val content: String,
    val author: UserDto,
    val createdAt: Instant,
    val problemAlias: String?,
) {
    companion object {
        fun from(article: Article) =
            ArticleDto(
                id = article.articleId!!,
                title = article.title,
                content = article.content,
                author = UserDto.from(article.author),
                createdAt = article.createdAt!!,
                problemAlias = article.problemAlias,
            )
    }
}
