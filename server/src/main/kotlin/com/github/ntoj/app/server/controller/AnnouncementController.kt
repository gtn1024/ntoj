package com.github.ntoj.app.server.controller

import com.fasterxml.jackson.annotation.JsonFormat
import com.github.ntoj.app.server.ext.success
import com.github.ntoj.app.server.model.entities.Announcement
import com.github.ntoj.app.server.model.L
import com.github.ntoj.app.server.service.AnnouncementService
import com.github.ntoj.app.shared.model.R
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/announcement")
class AnnouncementController(
    val announcementService: AnnouncementService,
) {
    @GetMapping
    fun getAnnouncements(
        @RequestParam(required = false, defaultValue = "1") current: Int,
        @RequestParam(required = false, defaultValue = "10") pageSize: Int,
    ): ResponseEntity<R<L<AnnouncementDto>>> {
        val list =
            announcementService.getAnnouncements(onlyVisible = true, desc = true, page = current, pageSize = pageSize)
        val count = announcementService.count(true)
        return R.success(200, "获取成功", L(count, current, list.map { AnnouncementDto.from(it) }))
    }

    @GetMapping("/{id}")
    fun getAnnouncement(
        @PathVariable id: Long,
    ): ResponseEntity<R<AnnouncementDto>> {
        return R.success(200, "获取成功", AnnouncementDto.from(announcementService.getAnnouncementsById(id)))
    }
}

data class AnnouncementDto(
    val id: Long?,
    val title: String?,
    val content: String?,
    val author: String?,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") val createdAt: Instant?,
) {
    companion object {
        fun from(announcement: Announcement) =
            AnnouncementDto(
                id = announcement.announcementId,
                title = announcement.title,
                content = announcement.content,
                author = announcement.author!!.username,
                createdAt = announcement.createdAt,
            )
    }
}
