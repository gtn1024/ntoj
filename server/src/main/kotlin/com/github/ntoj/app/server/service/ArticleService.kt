package com.github.ntoj.app.server.service

import com.github.ntoj.app.server.exception.AppException
import com.github.ntoj.app.server.model.Article
import com.github.ntoj.app.server.repository.ArticleRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction.ASC
import org.springframework.data.domain.Sort.Direction.DESC
import org.springframework.stereotype.Service

interface ArticleService {
    fun create(article: Article): Article

    fun get(id: Long): Article

    fun get(
        page: Int = 1,
        pageSize: Int = Int.MAX_VALUE,
        desc: Boolean = false,
    ): List<Article>

    fun count(): Long

    fun update(article: Article): Article

    fun remove(id: Long)
}

@Service
class ArticleServiceImpl(
    val articleRepository: ArticleRepository,
) : ArticleService {
    override fun create(article: Article): Article {
        return articleRepository.save(article)
    }

    override fun get(id: Long): Article {
        return articleRepository.findById(id).orElseThrow { AppException("文章不存在", 404) }
    }

    override fun get(
        page: Int,
        pageSize: Int,
        desc: Boolean,
    ): List<Article> {
        return articleRepository.findAll(
            PageRequest.of(
                page - 1,
                pageSize,
                Sort.by(if (desc) DESC else ASC, "articleId"),
            ),
        ).toList()
    }

    override fun count(): Long {
        return articleRepository.count()
    }

    override fun update(article: Article): Article {
        return articleRepository.save(article)
    }

    override fun remove(id: Long) {
        articleRepository.deleteById(id)
    }
}
