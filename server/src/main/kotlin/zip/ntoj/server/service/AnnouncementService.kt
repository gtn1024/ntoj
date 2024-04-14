package zip.ntoj.server.service

import jakarta.persistence.criteria.Predicate
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction.ASC
import org.springframework.data.domain.Sort.Direction.DESC
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import zip.ntoj.server.exception.AppException
import zip.ntoj.server.model.Announcement
import zip.ntoj.server.repository.AnnouncementRepository

interface AnnouncementService {
    fun getAnnouncementsById(id: Long): Announcement

    fun getAnnouncements(
        onlyVisible: Boolean = false,
        page: Int = 1,
        pageSize: Int = Int.MAX_VALUE,
        desc: Boolean = false,
    ): List<Announcement>

    fun count(onlyVisible: Boolean = false): Long

    fun newAnnouncement(announcement: Announcement): Announcement

    fun updateAnnouncement(announcement: Announcement): Announcement

    fun delete(id: Long)

    fun existsById(id: Long): Boolean
}

@Service
class AnnouncementServiceImpl(
    val announcementRepository: AnnouncementRepository,
) : AnnouncementService {
    override fun getAnnouncementsById(id: Long): Announcement {
        return announcementRepository.findById(id).orElseThrow { AppException("公告不存在", 404) }
    }

    override fun getAnnouncements(
        onlyVisible: Boolean,
        page: Int,
        pageSize: Int,
        desc: Boolean,
    ): List<Announcement> {
        return announcementRepository.findAll(
            buildSpecification(onlyVisible),
            PageRequest.of(
                page - 1,
                pageSize,
                Sort.by(if (desc) DESC else ASC, "announcementId"),
            ),
        ).toList()
    }

    override fun count(onlyVisible: Boolean): Long {
        return announcementRepository.count(buildSpecification(onlyVisible))
    }

    override fun newAnnouncement(announcement: Announcement): Announcement {
        return announcementRepository.save(announcement)
    }

    override fun updateAnnouncement(announcement: Announcement): Announcement {
        return announcementRepository.save(announcement)
    }

    override fun delete(id: Long) {
        announcementRepository.deleteById(id)
    }

    override fun existsById(id: Long): Boolean {
        return announcementRepository.existsById(id)
    }

    private fun buildSpecification(onlyVisible: Boolean): Specification<Announcement> {
        return Specification { root, _, criteriaBuilder ->
            val predicateList = mutableListOf<Predicate>()
            if (onlyVisible) {
                predicateList.add(criteriaBuilder.isTrue(root.get("visible")))
            }
            criteriaBuilder.and(*predicateList.toTypedArray())
        }
    }
}
