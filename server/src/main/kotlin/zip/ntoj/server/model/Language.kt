package zip.ntoj.server.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
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

    @Enumerated(EnumType.STRING)
    var type: LanguageType,

    var enabled: Boolean = true,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "language_id")
    var languageId: Long? = null,
) : BaseEntity() {
    enum class LanguageType {
        CPP,
        C,
        JAVA,
        PYTHON,
        OTHER,
    }
}
