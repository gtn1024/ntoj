package zip.ntoj.server.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity(name = "t_users")
class User(
    var username: String?,
    var password: String?,
    var salt: String?,
    var email: String?,
    @Column(name = "real_name") var realName: String?,
    var bio: String?,
    @Column(name = "user_role", columnDefinition = "int4")
    var role: UserRole = UserRole.USER,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    var userId: Long? = null,
) : BaseEntity()

enum class UserRole {
    BANNED, // 0
    USER, // 1
    COACH, // 2
    ADMIN, // 3
    SUPER_ADMIN, // 4
}
