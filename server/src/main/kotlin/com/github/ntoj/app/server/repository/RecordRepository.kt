package com.github.ntoj.app.server.repository

import com.github.ntoj.app.server.model.entities.Record
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface RecordRepository : JpaRepository<Record, String>, JpaSpecificationExecutor<Record>
