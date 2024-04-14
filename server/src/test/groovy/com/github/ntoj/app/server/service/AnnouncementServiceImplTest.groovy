package com.github.ntoj.app.server.service

import spock.lang.Specification
import com.github.ntoj.app.server.model.Announcement
import com.github.ntoj.app.server.repository.AnnouncementRepository

class AnnouncementServiceImplTest extends Specification {
    def announcementRepository = Mock(AnnouncementRepository)
    def announcementService = new AnnouncementServiceImpl(announcementRepository)

    def "test getAnnouncementsById"() {
        given: "设置请求参数"
        def announcement1 = new Announcement(announcementId: 1, title: "title1", content: "content1")
        def announcement2 = new Announcement(announcementId: 2, title: "title2", content: "content2")

        and: "mock announcementRepository 返回值"
        announcementRepository.findById(1) >> Optional.of(announcement1)
        announcementRepository.findById(2) >> Optional.of(announcement2)

        when: "获取公告信息"
        def response = announcementService.getAnnouncementsById(1)

        then: "验证"
        with(response) {
            announcementId == 1
            title == "title1"
            content == "content1"
        }
    }
}
