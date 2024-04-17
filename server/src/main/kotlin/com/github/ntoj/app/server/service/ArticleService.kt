package com.github.ntoj.app.server.service

import com.github.ntoj.app.server.exception.AppException
import com.github.ntoj.app.server.model.Article
import com.github.ntoj.app.server.repository.ArticleRepository
import org.springframework.stereotype.Service

interface ArticleService {
    fun create(article: Article): Article

    fun get(id: Long): Article
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
}
