package zip.ntoj.server.controller.admin

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.annotation.SaMode
import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import zip.ntoj.server.model.L
import zip.ntoj.server.model.Problem
import zip.ntoj.server.model.ProblemSample
import zip.ntoj.server.model.R
import zip.ntoj.server.service.ProblemService
import java.time.Instant

@RestController
@RequestMapping("/admin/problem")
@SaCheckLogin
@SaCheckRole(value = ["COACH", "ADMIN", "SUPER_ADMIN"], mode = SaMode.OR)
class AdminProblemController(
    val problemService: ProblemService,
) {
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
    fun delete(@PathVariable id: Long): ResponseEntity<R<Void>> {
        if (!problemService.exists(id)) return R.fail(404, "公告不存在")
        problemService.delete(id)
        return R.success(200, "删除成功")
    }
}

data class ProblemDto(
    val id: Long?,
    val title: String?,
    val alias: String?,
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC") val createdAt: Instant?,
) {
    companion object {
        fun from(problem: Problem): ProblemDto = ProblemDto(
            id = problem.problemId,
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
        )
    }
}
