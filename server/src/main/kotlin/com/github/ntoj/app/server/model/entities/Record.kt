package com.github.ntoj.app.server.model.entities

import com.github.ntoj.app.shared.model.JudgeStage
import com.github.ntoj.app.shared.model.RecordOrigin
import com.github.ntoj.app.shared.model.SubmissionStatus
import com.github.ntoj.app.shared.model.TestcaseJudgeResult
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes.JSON

@Entity(name = "t_records")
class Record(
    @ManyToOne @JoinColumn(name = "user_id", nullable = false) var user: User,
    @ManyToOne @JoinColumn(name = "problem_id") var problem: Problem?,
    @Enumerated(EnumType.STRING) @Column(nullable = false) var origin: RecordOrigin,
    @ManyToOne(cascade = [CascadeType.ALL]) @JoinColumn(name = "contest_id") var contest: Contest?,
    @Column(nullable = false) var lang: String,
    @Column(columnDefinition = "text", length = 16 * 1024) var selfTestInput: String?,
    @Column(columnDefinition = "text", length = 1024 * 1024, nullable = false) var code: String,
    @Enumerated(EnumType.STRING) @Column(nullable = false) var status: SubmissionStatus = SubmissionStatus.PENDING,
    @Enumerated(EnumType.STRING) @Column(nullable = false) var stage: JudgeStage = JudgeStage.PENDING,
    var time: Int? = null,
    var memory: Int? = null,
    var judgerId: String? = null,
    @Column(columnDefinition = "text", length = 1024 * 1024) var compileLog: String? = null,
    @JdbcTypeCode(JSON) var testcaseResult: List<TestcaseJudgeResult> = mutableListOf(),
    @Id @GenericGenerator(name = "snowFlakeIdGenerator", strategy = "com.github.ntoj.app.server.config.SnowFlakeIdGenerator")
    @GeneratedValue(generator = "snowFlakeIdGenerator")
    @Column(name = "record_id") var recordId: String? = null,
) : BaseEntity()
