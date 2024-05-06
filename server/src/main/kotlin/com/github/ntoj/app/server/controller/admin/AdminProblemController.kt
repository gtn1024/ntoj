package com.github.ntoj.app.server.controller.admin

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.annotation.SaMode
import cn.dev33.satoken.stp.StpUtil
import com.fasterxml.jackson.annotation.JsonFormat
import com.github.ntoj.app.server.exception.AppException
import com.github.ntoj.app.server.ext.fail
import com.github.ntoj.app.server.ext.from
import com.github.ntoj.app.server.ext.success
import com.github.ntoj.app.server.model.L
import com.github.ntoj.app.server.model.entities.Problem
import com.github.ntoj.app.server.model.entities.ProblemSample
import com.github.ntoj.app.server.service.FileService
import com.github.ntoj.app.server.service.FileUploadService
import com.github.ntoj.app.server.service.ProblemService
import com.github.ntoj.app.server.service.UserService
import com.github.ntoj.app.shared.model.R
import com.github.ntoj.app.shared.model.TestcaseDto
import com.github.ntoj.app.shared.util.ZipUtils
import com.github.ntoj.app.shared.util.randomString
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import org.apache.commons.io.FileUtils
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.time.Instant

@RestController
@RequestMapping("/admin/problem")
@SaCheckLogin
@SaCheckRole(value = ["COACH", "ADMIN", "SUPER_ADMIN"], mode = SaMode.OR)
class AdminProblemController(
    val problemService: ProblemService,
    val userService: UserService,
    val fileService: FileService,
    val fileUploadService: FileUploadService,
) {
    @GetMapping("{id}")
    fun get(
        @PathVariable id: Long,
    ): ResponseEntity<R<ProblemDto>> {
        val problem = problemService.get(id)
        return R.success(
            200,
            "获取成功",
            ProblemDto.from(problem),
        )
    }

    @PostMapping
    fun create(
        @RequestBody @Valid
        problemRequest: ProblemRequest,
    ): ResponseEntity<R<ProblemDto>> {
        val author = userService.getUserById(StpUtil.getLoginIdAsLong())
        val testcase = fileUploadService.get(problemRequest.testcase)
        return R.success(
            200,
            "创建成功",
            ProblemDto.from(
                problemService.new(
                    Problem(
                        alias = problemRequest.alias,
                        title = problemRequest.title,
                        background = problemRequest.background,
                        description = problemRequest.description,
                        inputDescription = problemRequest.inputDescription,
                        outputDescription = problemRequest.outputDescription,
                        timeLimit = problemRequest.timeLimit,
                        memoryLimit = problemRequest.memoryLimit,
                        samples = problemRequest.samples,
                        note = problemRequest.note,
                        visible = problemRequest.visible,
                        author = author,
                        judgeTimes = 1,
                        testCases = testcase,
                        codeLength = problemRequest.codeLength,
                    ),
                ),
            ),
        )
    }

    @PatchMapping("{id}")
    fun update(
        @RequestBody @Valid
        problemRequest: ProblemRequest,
        @PathVariable id: Long,
    ): ResponseEntity<R<ProblemDto>> {
        val problem = problemService.get(id)
        problem.alias = problemRequest.alias
        problem.title = problemRequest.title
        problem.background = problemRequest.background
        problem.description = problemRequest.description
        problem.inputDescription = problemRequest.inputDescription
        problem.outputDescription = problemRequest.outputDescription
        problem.timeLimit = problemRequest.timeLimit
        problem.memoryLimit = problemRequest.memoryLimit
        problem.samples = problemRequest.samples
        problem.note = problemRequest.note
        problem.visible = problemRequest.visible
        problem.codeLength = problemRequest.codeLength
        return R.success(
            200,
            "修改成功",
            ProblemDto.from(problemService.update(problem)),
        )
    }

    @GetMapping
    fun index(
        @RequestParam(required = false, defaultValue = "1") current: Int,
        @RequestParam(required = false, defaultValue = "20") pageSize: Int,
    ): ResponseEntity<R<L<ProblemDto>>> {
        val list = problemService.get(desc = true, page = current, pageSize = pageSize)
        val count = problemService.count(false)
        return R.success(
            200,
            "获取成功",
            L(
                total = count,
                page = current,
                list = list.map { ProblemDto.from(it) },
            ),
        )
    }

    @DeleteMapping("{id}")
    fun delete(
        @PathVariable id: Long,
    ): ResponseEntity<R<Void>> {
        if (!problemService.exists(id)) return R.fail(404, "题目不存在")
        problemService.delete(id)
        return R.success(200, "删除成功")
    }

    @PostMapping("uploadTestcase")
    fun updateTestcase(
        @RequestParam("file") multipartFile: MultipartFile,
    ): ResponseEntity<R<TestcaseDto>> {
        if (multipartFile.isEmpty) {
            throw AppException("文件为空", 400)
        }
        // check whether zip file
        if (multipartFile.originalFilename?.endsWith(".zip") != true ||
            (multipartFile.contentType != "application/zip" && multipartFile.contentType != "application/x-zip-compressed")
        ) {
            throw AppException("文件格式错误", 400)
        }
        // get files from zip
        val file: File = File.createTempFile("testcase_", ".zip")
        FileUtils.copyInputStreamToFile(multipartFile.inputStream, file)
        val fileList = ZipUtils.getFilenamesFromZip(file)
        // check whether files are valid
        if (!checkTestcaseFile(fileList)) {
            throw AppException("文件格式错误", 400)
        }
        val fileUpload =
            fileService.uploadTestCase(file, "${Instant.now().toEpochMilli()}-${randomString()}.zip")
        return R.success(200, "上传成功", TestcaseDto.from(fileUpload))
    }

    @GetMapping("/download_testcase/{id}")
    fun getTestcase(
        @PathVariable id: Long,
    ): ResponseEntity<Resource> {
        val testcase = fileUploadService.get(id)
        val file = fileService.get(testcase.path)
        val resource = InputStreamResource(file.inputStream())
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=${testcase.filename}")
            .contentType(MediaType.parseMediaType("application/zip"))
            .body(resource)
    }

    private fun checkTestcaseFile(fileList: List<String>): Boolean {
        if (fileList.size % 2 != 0) {
            return false
        }
        val files = fileList.filter { it.endsWith(".in") }.map { it.replace(".in", "") }
        for (i in 1..files.size) {
            if (!files.contains(i.toString())) {
                return false
            }
        }
        files.forEach {
            if (!fileList.contains("$it.in") || !fileList.contains("$it.out")) {
                return false
            }
        }
        return true
    }

    @GetMapping("search")
    fun search(
        @RequestParam keyword: String,
    ): ResponseEntity<R<List<ProblemDto>>> {
        val problems = problemService.search(keyword)
        return R.success(200, "获取成功", problems.map { ProblemDto.from(it) })
    }
}

data class ProblemRequest(
    @field:NotEmpty(message = "题号不能为空") val alias: String,
    @field:NotEmpty(message = "标题不能为空") val title: String,
    val background: String?,
    val description: String?,
    val inputDescription: String?,
    val outputDescription: String?,
    val timeLimit: Int? = 1000,
    val memoryLimit: Int? = 64,
    val samples: List<ProblemSample> = mutableListOf(),
    val note: String?,
    val visible: Boolean? = null,
    val testcase: Long,
    val codeLength: Int,
)

data class ProblemDto(
    val id: Long,
    val title: String,
    val alias: String,
    val background: String?,
    val description: String?,
    val inputDescription: String?,
    val outputDescription: String?,
    val timeLimit: Int?,
    val memoryLimit: Int?,
    val judgeTimes: Int?,
    val samples: List<ProblemSample>,
    val note: String?,
    val author: String?,
    val visible: Boolean?,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") val createdAt: Instant?,
    val testcase: TestcaseDto,
    val codeLength: Int,
) {
    companion object {
        fun from(problem: Problem): ProblemDto =
            ProblemDto(
                id = problem.problemId!!,
                title = problem.title,
                alias = problem.alias,
                background = problem.background,
                description = problem.description,
                inputDescription = problem.inputDescription,
                outputDescription = problem.outputDescription,
                timeLimit = problem.timeLimit,
                memoryLimit = problem.memoryLimit,
                judgeTimes = problem.judgeTimes,
                samples = problem.samples ?: listOf(),
                note = problem.note,
                author = problem.author?.username,
                createdAt = problem.createdAt,
                visible = problem.visible,
                testcase = TestcaseDto.from(problem.testCases!!),
                codeLength = problem.codeLength,
            )
    }
}
