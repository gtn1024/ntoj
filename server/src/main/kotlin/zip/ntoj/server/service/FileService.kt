package zip.ntoj.server.service

import cn.hutool.core.io.FileUtil
import cn.hutool.core.io.file.FileWriter
import cn.hutool.core.io.file.PathUtil
import org.apache.commons.io.FilenameUtils
import org.springframework.stereotype.Service
import zip.ntoj.server.config.FileConfig
import zip.ntoj.server.model.FileUpload
import zip.ntoj.server.util.fileMd5
import java.io.InputStream
import java.nio.file.Paths

interface FileService {
    fun uploadFile(stream: InputStream, filename: String, vararg path: String): FileUpload
    fun getFile(filename: String): FileUpload
    fun deleteFile(filename: String): Boolean

    fun uploadTestCase(stream: InputStream, filename: String): FileUpload {
        return uploadFile(stream, filename, "test_cases")
    }

    fun uploadAsset(stream: InputStream, filename: String): FileUpload {
        return uploadFile(stream, filename, "assets")
    }
}

@Service
class FileSystemFileService(
    val fileConfig: FileConfig,
    val fileUploadService: FileUploadService,
) : FileService {
    override fun uploadFile(stream: InputStream, filename: String, vararg path: String): FileUpload {
        val filePath = Paths.get(fileConfig.fileSystem.baseDir, *path)
        if (!PathUtil.exists(filePath, false)) {
            PathUtil.mkdir(filePath)
        }
        val file = filePath.resolve(filename).toFile()
        val fileWriter = FileWriter(file)
        fileWriter.writeFromStream(stream)
        // get file md5
        val fileMd5 = fileMd5(file.inputStream())
        val fileUpload = FileUpload(
            filename = FilenameUtils.getBaseName(file.toString()),
            path = file.toString(),
            hash = fileMd5,
            url = "/" + path.joinToString("/") + "/" + filename,
        )
        return fileUploadService.add(fileUpload)
    }

    override fun getFile(filename: String): FileUpload {
        return fileUploadService.get(filename)
    }

    override fun deleteFile(filename: String): Boolean {
        val file = fileUploadService.get(filename)
        val filePath = Paths.get(file.path)
        fileUploadService.delete(file.fileId!!)
        if (PathUtil.exists(filePath, false)) {
            return FileUtil.del(filePath)
        }
        return false
    }
}
