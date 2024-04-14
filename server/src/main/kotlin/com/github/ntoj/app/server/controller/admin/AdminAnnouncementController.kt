package com.github.ntoj.app.server.controller.admin

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.annotation.SaMode
import cn.dev33.satoken.stp.StpUtil
import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import com.github.ntoj.app.server.ext.fail
import com.github.ntoj.app.server.ext.success
import com.github.ntoj.app.server.model.Announcement
import com.github.ntoj.app.server.model.L
import com.github.ntoj.app.server.service.AnnouncementService
import com.github.ntoj.app.server.service.UserService
import com.github.ntoj.app.shared.model.R
import java.time.Instant

@RestController
@RequestMapping("/admin/announcement")
@SaCheckLogin
@SaCheckRole(value = ["COACH", "ADMIN", "SUPER_ADMIN"], mode = SaMode.OR)
class AdminAnnouncementController(
    val userService: UserService,
    val announcementService: AnnouncementService,
) {
    @GetMapping("{id}")
    fun get(
        @PathVariable id: Long,
    ): ResponseEntity<R<AnnouncementDto>> {
        val announcement = announcementService.getAnnouncementsById(id)
        return R.success(
            200,
            "获取成功",
            AnnouncementDto.from(announcement),
        )
    }

    @GetMapping
    fun getAnnouncements(
        @RequestParam(required = false, defaultValue = "1") current: Int,
        @RequestParam(required = false, defaultValue = "20") pageSize: Int,
    ): ResponseEntity<R<L<AnnouncementDto>>> {
        val list = announcementService.getAnnouncements(desc = true, page = current, pageSize = pageSize)
        val count = announcementService.count(false)
        return R.success(
            200,
            "获取成功",
            L(
                total = count,
                page = current,
                list = list.map { AnnouncementDto.from(it) },
            ),
        )
    }

    @PostMapping
    fun createAnnouncement(
        @RequestBody @Valid
        announcementRequest: AnnouncementRequest,
    ): ResponseEntity<R<AnnouncementDto>> {
        val author = userService.getUserById(StpUtil.getLoginIdAsLong())
        return R.success(
            200,
            "创建成功",
            AnnouncementDto.from(
                announcementService.newAnnouncement(
                    Announcement(
                        title = announcementRequest.title!!,
                        content = announcementRequest.content!!,
                        visible = announcementRequest.visible ?: false,
                        author = author,
                    ),
                ),
            ),
        )
    }

    @PatchMapping("{id}")
    fun updateAnnouncement(
        @RequestBody @Valid
        announcementRequest: AnnouncementRequest,
        @PathVariable id: Long,
    ): ResponseEntity<R<AnnouncementDto>> {
        val announcement = announcementService.getAnnouncementsById(id)
        if (announcement.title != announcementRequest.title) {
            announcement.title = announcementRequest.title
        }
        if (announcement.content != announcementRequest.content) {
            announcement.content = announcementRequest.content
        }
        if (announcement.visible != announcementRequest.visible) {
            announcement.visible = announcementRequest.visible
        }
        return R.success(
            200,
            "修改成功",
            AnnouncementDto.from(announcementService.updateAnnouncement(announcement)),
        )
    }

    @DeleteMapping("{id}")
    fun deleteAnnouncement(
        @PathVariable id: Long,
    ): ResponseEntity<R<Void>> {
        if (!announcementService.existsById(id)) return R.fail(404, "公告不存在")
        announcementService.delete(id)
        return R.success(200, "删除成功")
    }
}

data class AnnouncementRequest(
    @field:NotEmpty(message = "标题不能为空") val title: String?,
    @field:NotEmpty(message = "内容不能为空") val content: String?,
    val visible: Boolean? = null,
)

data class AnnouncementDto(
    val id: Long?,
    val title: String?,
    val content: String?,
    val author: String?,
    val visible: Boolean?,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") val createdAt: Instant?,
) {
    companion object {
        fun from(announcement: Announcement) =
            AnnouncementDto(
                id = announcement.announcementId,
                title = announcement.title,
                content = announcement.content,
                author = announcement.author!!.username,
                visible = announcement.visible,
                createdAt = announcement.createdAt,
            )
    }
}
