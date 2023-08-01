package zip.ntoj.server.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import zip.ntoj.shared.dtos.judge.SubmissionStatus

@Entity(name = "t_submissions")
class Submission(
    @ManyToOne
    var user: User?,
    @ManyToOne
    var problem: Problem? = null,
    var origin: SubmissionOrigin = SubmissionOrigin.PROBLEM,
    var language: String? = null,
    @Column(columnDefinition = "text", length = 65535)
    var code: String? = null,
    @Enumerated(EnumType.STRING)
    var status: SubmissionStatus = SubmissionStatus.PENDING,

    var time: Int? = null,
    var memory: Int? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submission_id")
    var submissionId: Long? = null,
) : BaseEntity() {
    enum class SubmissionOrigin {
        PROBLEM, // 0
    }
}
