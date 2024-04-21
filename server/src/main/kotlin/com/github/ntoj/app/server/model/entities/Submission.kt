package com.github.ntoj.app.server.model.entities

import com.github.ntoj.app.shared.model.JudgeStage
import com.github.ntoj.app.shared.model.SubmissionStatus
import com.github.ntoj.app.shared.model.TestcaseJudgeResult
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes.JSON

@Entity(name = "t_submissions")
class Submission(
    @ManyToOne
    var user: User?,
    @ManyToOne
    var problem: Problem? = null,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var origin: SubmissionOrigin = SubmissionOrigin.PROBLEM,
    var contestId: Long? = null,
    @OneToOne var language: Language? = null,
    // 64KB
    @Column(columnDefinition = "text", length = 65_536)
    var code: String? = null,
    @Enumerated(EnumType.STRING)
    var status: SubmissionStatus = SubmissionStatus.PENDING,
    var time: Int? = null,
    var memory: Int? = null,
    var judgerId: String? = null,
    @Enumerated(EnumType.STRING)
    var judgeStage: JudgeStage = JudgeStage.PENDING,
    @Column(columnDefinition = "text", length = 65535)
    var compileLog: String? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submission_id")
    var submissionId: Long? = null,
    @JdbcTypeCode(JSON)
    var testcaseResult: List<TestcaseJudgeResult>? = mutableListOf(),
) : BaseEntity() {
    enum class SubmissionOrigin {
        PROBLEM, // 0
        CONTEST, // 1
    }
}
