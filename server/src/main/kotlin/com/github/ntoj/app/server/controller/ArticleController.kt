package com.github.ntoj.app.server.controller

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import com.github.ntoj.app.server.ext.success
import com.github.ntoj.app.server.model.Article
import com.github.ntoj.app.server.model.User
import com.github.ntoj.app.server.service.ArticleService
import com.github.ntoj.app.server.service.UserService
import com.github.ntoj.app.shared.model.R
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/article")
class ArticleController(
    val articlesService: ArticleService,
    val userService: UserService,
) {
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

        article = articlesService.create(article)
        return R.success(200, "创建成功", ArticleDto.from(article))
    }
}

data class ArticleRequest(
    val title: String?,
    val content: String?,
)

data class ArticleDto(
    val id: Long,
    val title: String,
    val content: String,
    val author: UserDto,
    val createdAt: Instant,
) {
    data class UserDto(
        val username: String,
        val realName: String?,
    ) {
        companion object {
            fun from(user: User): UserDto =
                UserDto(
                    username = user.username,
                    realName = user.realName,
                )
        }
    }

    companion object {
        fun from(article: Article) =
            ArticleDto(
                id = article.articleId!!,
                title = article.title,
                content = article.content,
                author = UserDto.from(article.author),
                createdAt = article.createdAt!!,
            )
    }
}
