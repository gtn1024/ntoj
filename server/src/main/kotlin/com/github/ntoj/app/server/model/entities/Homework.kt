package com.github.ntoj.app.server.model.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import java.time.Instant

@Entity(name = "t_homeworks")
class Homework(
    var title: String,
    var startTime: Instant,
    var endTime: Instant,
    @ManyToMany(targetEntity = Group::class)
    @JoinTable(
        name = "t_homeworks_groups",
        joinColumns = [JoinColumn(name = "homework_id")],
        inverseJoinColumns = [JoinColumn(name = "group_id")],
    )
    var groups: List<Group> = mutableListOf(),
    @ManyToMany(targetEntity = Problem::class)
    @JoinTable(
        name = "t_homeworks_problems",
        joinColumns = [JoinColumn(name = "homework_id")],
        inverseJoinColumns = [JoinColumn(name = "problem_id")],
    )
    var problems: List<Problem> = mutableListOf(),
    @ManyToOne @JoinColumn(name = "author_user_id", nullable = false)
    var author: User,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "homework_id")
    var homeworkId: Long? = null,
) : BaseEntity()
