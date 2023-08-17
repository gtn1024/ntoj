package zip.ntoj.server.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity(name = "t_judge_client_tokens")
class JudgeClientToken(
    var name: String,
    var token: String,
    var enabled: Boolean = true,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    var tokenId: Long? = null,
) : BaseEntity()
