package com.github.ntoj.app.server.service

import com.github.ntoj.app.server.exception.AppException
import com.github.ntoj.app.server.model.entities.FileUpload
import com.github.ntoj.app.server.repository.FileUploadRepository
import org.springframework.stereotype.Service

interface FileUploadService {
    fun add(file: FileUpload): FileUpload

    fun delete(id: Long)

    fun update(file: FileUpload): FileUpload

    fun get(id: Long): FileUpload

    fun get(filename: String): FileUpload
}

@Service
class FileUploadServiceImpl(
    val fileUploadRepository: FileUploadRepository,
) : FileUploadService {
    override fun add(file: FileUpload): FileUpload {
        return fileUploadRepository.save(file)
    }

    override fun delete(id: Long) {
        fileUploadRepository.deleteById(id)
    }

    override fun update(file: FileUpload): FileUpload {
        return fileUploadRepository.save(file)
    }

    override fun get(id: Long): FileUpload {
        return fileUploadRepository.findById(id).orElseThrow { AppException("文件不存在", 404) }
    }

    override fun get(filename: String): FileUpload {
        return fileUploadRepository.findByFilename(filename).orElseThrow { AppException("文件不存在", 404) }
    }
}
