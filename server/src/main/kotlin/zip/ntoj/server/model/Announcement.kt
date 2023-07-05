package zip.ntoj.server.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne

@Entity(name = "t_announcements")
class Announcement(
    var title: String?,
    @Column(columnDefinition = "text") var content: String?,
    @ManyToOne
    var author: User?,
    var visible: Boolean? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "announcement_id")
    var announcementId: Long? = null,
) : BaseEntity()
