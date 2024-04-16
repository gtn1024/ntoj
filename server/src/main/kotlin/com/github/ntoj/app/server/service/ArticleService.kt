package com.github.ntoj.app.server.service

import com.github.ntoj.app.server.repository.ArticleRepository
import org.springframework.stereotype.Service

interface ArticleService

@Service
class ArticleServiceImpl(
    val articleRepository: ArticleRepository,
) : ArticleService
