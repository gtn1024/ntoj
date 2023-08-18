package zip.ntoj.server.controller.admin

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.annotation.SaMode
import cn.hutool.core.io.file.FileNameUtil
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import zip.ntoj.server.ext.success
import zip.ntoj.server.model.FileUpload
import zip.ntoj.server.service.FileService
import zip.ntoj.shared.model.R
import zip.ntoj.shared.util.randomString
import java.time.Instant

@RestController
@RequestMapping("/admin/file")
@SaCheckLogin
@SaCheckRole(value = ["COACH", "ADMIN", "SUPER_ADMIN"], mode = SaMode.OR)
class AdminFileController(
    val fileService: FileService,
) {
    @PostMapping
    fun uploadFile(@RequestParam("file") multipartFile: MultipartFile): ResponseEntity<R<FileDto>> {
        val filename =
            "${Instant.now().toEpochMilli()}-${randomString()}.${FileNameUtil.extName(multipartFile.originalFilename)}"
        val fileUpload = fileService.uploadAsset(multipartFile.inputStream, filename)
        return R.success(
            200,
            "上传成功",
            FileDto.from(fileUpload),
        )
    }
}

data class FileDto(
    val filename: String?,
    val url: String?,
    val hash: String?,
) {
    companion object {
        fun from(fileUpload: FileUpload): FileDto {
            return FileDto(
                filename = fileUpload.filename,
                url = fileUpload.url,
                hash = fileUpload.hash,
            )
        }
    }
}
