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
import zip.ntoj.shared.model.JudgeStage
import zip.ntoj.shared.model.SubmissionStatus

@Entity(name = "t_self_test_submissions")
class SelfTestSubmission(
    @ManyToOne
    var user: User,
    @OneToOne var language: Language,
    // 64KB
    @Column(columnDefinition = "text", length = 65_536, nullable = false)
    var code: String,
    @Enumerated(EnumType.STRING)
    var status: SubmissionStatus = SubmissionStatus.PENDING,
    @Column(nullable = false) var timeLimit: Int,
    @Column(nullable = false) var memoryLimit: Int,
    var time: Int? = null,
    var memory: Int? = null,
    @Enumerated(EnumType.STRING)
    var judgeStage: JudgeStage = JudgeStage.PENDING,
    @Column(columnDefinition = "text", length = 65535)
    var compileLog: String? = null,
    @Column(columnDefinition = "text", length = 65535, nullable = false)
    var input: String,
    @Column(columnDefinition = "text", length = 65535)
    var output: String? = null,
    @Column(columnDefinition = "text", length = 65535)
    var expectedOutput: String? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "self_test_submission_id")
    var selfTestSubmissionId: Long? = null,
) : BaseEntity()
