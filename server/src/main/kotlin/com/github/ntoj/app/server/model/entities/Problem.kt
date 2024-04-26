package com.github.ntoj.app.server.model.entities

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes.JSON

@Entity(name = "t_problems")
class Problem(
    @Column(nullable = false) var title: String,
    @Column(name = "p_alias", unique = true, nullable = false) var alias: String,
    @Column(columnDefinition = "text") var background: String?,
    @Column(columnDefinition = "text") var description: String?,
    @Column(columnDefinition = "text") var inputDescription: String?,
    @Column(columnDefinition = "text") var outputDescription: String?,
    var timeLimit: Int?,
    var memoryLimit: Int?,
    var judgeTimes: Int?,
    var codeLength: Int = 16,
    @ManyToMany
    @JoinTable(
        name = "t_problems_languages",
        joinColumns = [JoinColumn(name = "problem_id")],
        inverseJoinColumns = [JoinColumn(name = "language_id")],
    )
    var languages: List<Language> = mutableListOf(),
    var allowAllLanguages: Boolean,
    @OneToOne var testCases: FileUpload?,
    @JdbcTypeCode(JSON) var samples: List<ProblemSample>? = mutableListOf(),
    @Column(columnDefinition = "text") var note: String?,
    @ManyToOne
    var author: User?,
    var visible: Boolean? = null,
    @Column(nullable = false) var submitTimes: Long = 0,
    @Column(nullable = false) var acceptedTimes: Long = 0,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "problem_id")
    var problemId: Long? = null,
) : BaseEntity()

data class ProblemSample(
    @JsonProperty("input") var input: String?,
    @JsonProperty("output") var output: String?,
)
