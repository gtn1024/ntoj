package com.github.ntoj.app.server.controller.judge

import cn.dev33.satoken.annotation.SaCheckPermission
import com.github.ntoj.app.server.config.RecordQueueManager
import com.github.ntoj.app.server.config.system.LanguageMap
import com.github.ntoj.app.server.ext.fail
import com.github.ntoj.app.server.ext.from
import com.github.ntoj.app.server.ext.success
import com.github.ntoj.app.server.service.FileService
import com.github.ntoj.app.server.service.FileUploadService
import com.github.ntoj.app.server.service.ProblemService
import com.github.ntoj.app.server.service.RecordService
import com.github.ntoj.app.shared.model.JudgeStage
import com.github.ntoj.app.shared.model.JudgerRecordDto
import com.github.ntoj.app.shared.model.R
import com.github.ntoj.app.shared.model.RecordOrigin
import com.github.ntoj.app.shared.model.SubmissionStatus
import com.github.ntoj.app.shared.model.TestcaseDto
import com.github.ntoj.app.shared.model.UpdateRecordRequest
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/judge_client")
@SaCheckPermission(value = ["PERM_JUDGE"])
class JudgeClientController(
    val fileUploadService: FileUploadService,
    val fileService: FileService,
    private val languages: LanguageMap,
    private val recordQueueManager: RecordQueueManager,
    private val recordService: RecordService,
    private val problemService: ProblemService,
) {
    @GetMapping("/ping")
    fun ping(): ResponseEntity<R<PingResponse>> {
        return R.success(200, "Pong", PingResponse("Pong"))
    }

    @PatchMapping("/update/{recordId}")
    fun update(
        @PathVariable recordId: String,
        @RequestParam(required = false) status: SubmissionStatus?,
        @RequestParam(required = false) stage: JudgeStage?,
        @RequestBody(required = false) request: UpdateRecordRequest?,
    ): ResponseEntity<R<Void>> {
        val record = recordService.get(recordId) ?: return R.fail(404, "记录不存在")
        require(status != null || stage != null) { "status 和 stage 至少需要一个" }
        if (stage != null) record.stage = stage
        if (status != null) record.status = status
        if (record.origin != RecordOrigin.SELF_TEST && status == SubmissionStatus.ACCEPTED) {
            val problem = problemService.get(record.problem!!.problemId!!)
            problem.acceptedTimes += 1
            problemService.update(problem)
        }
        if (request != null) {
            record.time = request.time
            record.memory = request.memory
            record.judgerId = request.judgerId
            record.testcaseResult = request.testcaseResult
            record.compileLog = request.compileLog
        }
        recordService.update(record)
        return R.success(200, "更新成功")
    }

    @GetMapping("/get")
    fun get(): ResponseEntity<R<JudgerRecordDto>> {
        val record = recordQueueManager.getOneOrNull() ?: return R.success(204, "获取成功")
        recordService.setJudging(record.recordId!!)
        val languageStructure = languages[record.lang]
        if (languageStructure == null) {
            record.status = SubmissionStatus.SYSTEM_ERROR
            recordService.update(record)
            return R.success(204, "获取成功", null)
        }
        val response =
            JudgerRecordDto(
                record.recordId!!,
                record.problem?.problemId,
                record.code,
                languageStructure,
                testcase = if (record.problem != null) TestcaseDto.from(record.problem!!.testCases) else null,
                timeLimit = record.problem?.timeLimit ?: 1000,
                memoryLimit = record.problem?.memoryLimit ?: 256,
                origin = record.origin,
                input = record.selfTestInput,
            )
        return R.success(200, "获取成功", response)
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
}

data class PingResponse(
    val message: String,
    val currentTime: Instant = Instant.now(),
)
