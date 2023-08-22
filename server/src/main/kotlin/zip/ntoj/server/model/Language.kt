package zip.ntoj.server.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity(name = "t_languages")
class Language(
    @Column(name = "language_name")
    var languageName: String,

    @Column(columnDefinition = "TEXT")
    var compileCommand: String? = null,

    @Column(columnDefinition = "TEXT")
    var executeCommand: String? = null,

    var enabled: Boolean = true,

    var memoryLimitRate: Int? = null,
    var timeLimitRate: Int? = null,

    var sourceFilename: String?,
    var targetFilename: String?,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "language_id")
    var languageId: Long? = null,
) : BaseEntity()
