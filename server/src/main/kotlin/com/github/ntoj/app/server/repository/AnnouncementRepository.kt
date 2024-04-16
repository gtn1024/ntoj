package com.github.ntoj.app.server.repository

import com.github.ntoj.app.server.model.Announcement
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface AnnouncementRepository : JpaRepository<Announcement, Long>, JpaSpecificationExecutor<Announcement>
