package zip.ntoj.server.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
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

    @ManyToMany var problems: List<Problem> = listOf(),

    @ManyToMany var users: List<User> = listOf(),

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
