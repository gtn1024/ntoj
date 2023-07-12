package zip.ntoj.server.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submission_id")
    var submissionId: Long? = null,
) : BaseEntity() {
    enum class SubmissionOrigin {
        PROBLEM, // 0
    }

    enum class SubmissionStatus {
        PENDING, // 0
        JUDGING, // 1
        ACCEPTED, // 2
        WRONG_ANSWER, // 3
        TIME_LIMIT_EXCEEDED, // 4
        MEMORY_LIMIT_EXCEEDED, // 5
        RUNTIME_ERROR, // 6
        COMPILE_ERROR, // 7
        SYSTEM_ERROR, // 8
        PRESENTATION_ERROR, // 9
        DEPRECATED, // 10
    }
}
