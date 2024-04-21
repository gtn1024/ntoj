package com.github.ntoj.app.server.repository

import com.github.ntoj.app.server.model.entities.Contest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface ContestRepository : JpaRepository<Contest, Long>, JpaSpecificationExecutor<Contest>
