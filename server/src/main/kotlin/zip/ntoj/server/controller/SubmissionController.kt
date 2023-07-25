package zip.ntoj.server.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import zip.ntoj.server.model.R
import zip.ntoj.server.service.SubmissionService

@RestController
@RequestMapping("/submission")
class SubmissionController(
    private val submissionService: SubmissionService,
) {
    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): ResponseEntity<R<SubmissionDto>> {
        val submission = submissionService.get(id)
        return R.success(200, "获取成功", SubmissionDto.from(submission))
    }
}
