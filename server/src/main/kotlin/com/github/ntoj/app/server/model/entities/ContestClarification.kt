package com.github.ntoj.app.server.model.entities

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany

@Entity(name = "t_contest_clarifications")
class ContestClarification(
    @Column(nullable = false) var title: String,
    @Column(nullable = false, columnDefinition = "TEXT") var content: String,
    @ManyToOne @JoinColumn(name = "user_id", nullable = false) var user: User,
    @ManyToOne
    @JoinColumn(name = "contest_id", nullable = false)
    @JoinTable(
        name = "t_contests_clarifications",
        joinColumns = [JoinColumn(name = "clarification_id")],
        inverseJoinColumns = [JoinColumn(name = "contest_id")],
    )
    var contest: Contest,
    @Column(nullable = false) var sticky: Boolean = false,
    @Column(nullable = false) var closed: Boolean = false,
    @Column(nullable = false) var visible: Boolean = true,
    @OneToMany(cascade = [CascadeType.ALL])
    @JoinTable(
        name = "t_contest_clarifications_responses",
        joinColumns = [JoinColumn(name = "clarification_id")],
        inverseJoinColumns = [JoinColumn(name = "response_id")],
    )
    var responses: MutableList<ContestClarificationResponse> = mutableListOf(),
    var contestProblemId: Int? = null,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "clarification_id") var clarificationId: Long? = null,
) : BaseEntity()

@Entity(name = "t_contest_clarification_responses")
class ContestClarificationResponse(
    @Column(nullable = false, columnDefinition = "TEXT") var content: String,
    @ManyToOne @JoinColumn(nullable = false) var user: User,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "response_id") var responseId: Long? = null,
) : BaseEntity()
