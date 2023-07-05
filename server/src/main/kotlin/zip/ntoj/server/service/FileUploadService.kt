package zip.ntoj.server.service

import org.springframework.stereotype.Service
import zip.ntoj.server.exception.TojException
import zip.ntoj.server.model.FileUpload
import zip.ntoj.server.repository.FileUploadRepository

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
        return fileUploadRepository.findById(id).orElseThrow { TojException("文件不存在", 404) }
    }

    override fun get(filename: String): FileUpload {
        return fileUploadRepository.findByFilename(filename).orElseThrow { TojException("文件不存在", 404) }
    }
}
