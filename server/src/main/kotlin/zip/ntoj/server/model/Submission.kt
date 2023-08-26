package zip.ntoj.server.model

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
import zip.ntoj.shared.model.JudgeStage
import zip.ntoj.shared.model.SubmissionStatus
import zip.ntoj.shared.model.TestcaseJudgeResult

@Entity(name = "t_submissions")
class Submission(
    @ManyToOne
    var user: User?,
    @ManyToOne
    var problem: Problem? = null,
    var origin: SubmissionOrigin = SubmissionOrigin.PROBLEM,
    @OneToOne var language: Language? = null,
    @Column(columnDefinition = "text", length = 65_536) // 64KB
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
    }
}
