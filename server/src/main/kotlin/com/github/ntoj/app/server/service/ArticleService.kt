package com.github.ntoj.app.server.service

import com.github.ntoj.app.server.exception.AppException
import com.github.ntoj.app.server.model.entities.Article
import com.github.ntoj.app.server.repository.ArticleRepository
import jakarta.persistence.criteria.Predicate
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction.ASC
import org.springframework.data.domain.Sort.Direction.DESC
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

interface ArticleService {
    fun create(article: Article): Article

    fun get(id: Long): Article

    fun get(
        onlyVisible: Boolean = false,
        page: Int = 1,
        pageSize: Int = Int.MAX_VALUE,
        desc: Boolean = false,
        problemAlias: String? = null,
    ): List<Article>

    fun count(
        onlyVisible: Boolean = false,
        problemAlias: String? = null,
    ): Long

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
        onlyVisible: Boolean,
        page: Int,
        pageSize: Int,
        desc: Boolean,
        problemAlias: String?,
    ): List<Article> {
        return articleRepository.findAll(
            buildSpecification(onlyVisible, problemAlias),
            PageRequest.of(
                page - 1,
                pageSize,
                Sort.by(if (desc) DESC else ASC, "articleId"),
            ),
        ).toList()
    }

    override fun count(
        onlyVisible: Boolean,
        problemAlias: String?,
    ): Long {
        return articleRepository.count(buildSpecification(onlyVisible, problemAlias))
    }

    override fun update(article: Article): Article {
        return articleRepository.save(article)
    }

    override fun remove(id: Long) {
        articleRepository.deleteById(id)
    }

    private fun buildSpecification(
        onlyVisible: Boolean,
        problemAlias: String?,
    ): Specification<Article> {
        return Specification { root, _, criteriaBuilder ->
            val predicateList = mutableListOf<Predicate>()
            if (onlyVisible) {
                predicateList.add(criteriaBuilder.isTrue(root.get("visible")))
            }
            if (problemAlias != null && problemAlias != "") {
                predicateList.add(criteriaBuilder.equal(root.get<String?>("problemAlias"), problemAlias))
            }
            criteriaBuilder.and(*predicateList.toTypedArray())
        }
    }
}
