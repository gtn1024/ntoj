package com.github.ntoj.app.server.repository

import com.github.ntoj.app.server.model.entities.FileUpload
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.Optional

interface FileUploadRepository : JpaRepository<FileUpload, Long>, JpaSpecificationExecutor<FileUpload> {
    fun findByFilename(filename: String): Optional<FileUpload>
}
