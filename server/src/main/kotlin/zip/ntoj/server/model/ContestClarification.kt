package zip.ntoj.server.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany

@Entity(name = "t_contest_clarifications")
class ContestClarification(
    @Column(nullable = false) var title: String,
    @Column(nullable = false, columnDefinition = "TEXT") var content: String,
    @ManyToOne @JoinColumn(nullable = false) var user: User,
    @ManyToOne @JoinColumn(nullable = false) var contest: Contest,
    @Column(nullable = false) var sticky: Boolean = false,
    @Column(nullable = false) var visible: Boolean = true,
    @OneToMany var responses: List<ContestClarificationResponse> = mutableListOf(),
    var contestProblemId: Int? = null,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "clarification_id") var clarificationId: Long? = null,
) : BaseEntity()

@Entity(name = "t_contest_clarification_responses")
class ContestClarificationResponse(
    @Column(nullable = false, columnDefinition = "TEXT") var content: String,
    @ManyToOne @JoinColumn(nullable = false) var user: User,
    @ManyToOne @JoinColumn(nullable = false) var clarification: ContestClarification,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "response_id") var responseId: Long? = null,
) : BaseEntity()
