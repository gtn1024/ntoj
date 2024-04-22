package com.github.ntoj.app.server.model.dtos.admin

import com.github.ntoj.app.server.model.dtos.UserDto
import com.github.ntoj.app.server.model.entities.Article
import java.io.Serializable
import java.time.Instant

data class ArticleDto(
    val createdAt: Instant,
    val title: String,
    val content: String,
    val author: UserDto,
    val id: Long,
) : Serializable {
    companion object {
        fun from(article: Article) =
            ArticleDto(
                createdAt = article.createdAt!!,
                title = article.title,
                content = article.content,
                author = UserDto.from(article.author),
                id = article.articleId!!,
            )
    }
}
