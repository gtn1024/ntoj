package com.github.ntoj.app.server.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import com.github.ntoj.app.server.model.FileUpload
import java.util.Optional

interface FileUploadRepository : JpaRepository<FileUpload, Long>, JpaSpecificationExecutor<FileUpload> {
    fun findByFilename(filename: String): Optional<FileUpload>
}
