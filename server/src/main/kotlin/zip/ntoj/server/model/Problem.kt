package zip.ntoj.server.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes.JSON

@Entity(name = "t_problems")
class Problem(
    var title: String?,
    @Column(name = "p_alias") var alias: String?,
    @Column(columnDefinition = "text") var background: String?,
    @Column(columnDefinition = "text") var description: String?,
    @Column(columnDefinition = "text") var inputDescription: String?,
    @Column(columnDefinition = "text") var outputDescription: String?,
    var timeLimit: Int?,
    var memoryLimit: Int?,
    var judgeTimes: Int?,

    @ManyToMany var languages: List<Language> = mutableListOf(),

    @OneToOne var testCases: FileUpload?,

    @JdbcTypeCode(JSON) var samples: List<ProblemSample>? = mutableListOf(),

    @Column(columnDefinition = "text") var note: String?,
    @ManyToOne
    var author: User?,
    var visible: Boolean? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "problem_id")
    var problemId: Long? = null,
) : BaseEntity()

data class ProblemSample(
    @JsonProperty("input") var input: String?,
    @JsonProperty("output") var output: String?,
)
