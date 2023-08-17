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
import zip.ntoj.shared.dtos.judge.SubmissionStatus

@Entity(name = "t_submissions")
class Submission(
    @ManyToOne
    var user: User?,
    @ManyToOne
    var problem: Problem? = null,
    var origin: SubmissionOrigin = SubmissionOrigin.PROBLEM,
    @OneToOne var language: Language? = null,
    @Column(columnDefinition = "text", length = 65535)
    var code: String? = null,
    @Enumerated(EnumType.STRING)
    var status: SubmissionStatus = SubmissionStatus.PENDING,

    var time: Int? = null,
    var memory: Int? = null,

    var judgerId: String? = null,

    @Enumerated(EnumType.STRING)
    var judgeStage: JudgeStage = JudgeStage.PENDING,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submission_id")
    var submissionId: Long? = null,
) : BaseEntity() {
    enum class SubmissionOrigin {
        PROBLEM, // 0
    }

    enum class JudgeStage {
        PENDING,
        COMPILING,
        JUDGING,
        FINISHED,
    }
}
