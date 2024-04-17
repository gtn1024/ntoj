package com.github.ntoj.app.server.service

import com.github.ntoj.app.server.model.Article
import com.github.ntoj.app.server.repository.ArticleRepository
import org.springframework.stereotype.Service

interface ArticleService {
    fun create(article: Article): Article
}

@Service
class ArticleServiceImpl(
    val articleRepository: ArticleRepository,
) : ArticleService {
    override fun create(article: Article): Article {
        return articleRepository.save(article)
    }
}
