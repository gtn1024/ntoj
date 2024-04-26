package com.github.ntoj.app.server.model.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity(name = "t_articles")
class Article(
    var title: String,
    @Column(columnDefinition = "text") var content: String,
    @ManyToOne
    @JoinColumn(name = "author_user_id")
    var author: User,
    @Column(nullable = false) var visible: Boolean = true,
    var problemAlias: String? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    var articleId: Long? = null,
) : BaseEntity()
