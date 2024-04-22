package com.github.ntoj.app.server.repository

import com.github.ntoj.app.server.model.entities.Group
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface GroupRepository : JpaRepository<Group, Long>, JpaSpecificationExecutor<Group>
