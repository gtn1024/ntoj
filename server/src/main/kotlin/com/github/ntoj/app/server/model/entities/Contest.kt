package com.github.ntoj.app.server.model.entities

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes.JSON
import java.time.Instant

@Entity(name = "t_contests")
class Contest(
    @Column(nullable = false) var title: String,
    @Column(columnDefinition = "TEXT") var description: String? = null,
    @Column(nullable = false) var startTime: Instant,
    @Column(nullable = false) var endTime: Instant,
    var freezeTime: Int? = null,
    @Column(nullable = false) @Enumerated(EnumType.STRING) var type: ContestType = ContestType.ICPC,
    @Column(nullable = false) @Enumerated(EnumType.STRING) var permission: ContestPermission = ContestPermission.PUBLIC,
    var password: String? = null,
    @ManyToOne @JoinColumn(name = "author_user_id", nullable = false)
    var author: User,
    @JdbcTypeCode(JSON) @Column(nullable = false) var problems: List<ContestProblem> = listOf(),
    @JdbcTypeCode(JSON) @Column(nullable = false) var users: MutableList<ContestUser> = mutableListOf(),
    @Column(nullable = false) var visible: Boolean = true,
    @Column(nullable = false) var showFinalBoard: Boolean = false,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "contest_id") var contestId: Long? = null,
) : BaseEntity() {
    enum class ContestType {
        ICPC,
    }

    enum class ContestPermission {
        PUBLIC,
        PRIVATE,
        PASSWORD,
    }
}

data class ContestProblem
    @JsonCreator
    constructor(
        @JsonProperty("problemId") var problemId: Long,
        @JsonProperty("contestProblemIndex") var contestProblemIndex: Int,
    )

data class ContestUser
    @JsonCreator
    constructor(
        @JsonProperty("userId") var userId: Long,
        @JsonProperty("joinAt") var joinAt: Long,
    )
