package zip.ntoj.server.controller.admin

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.annotation.SaMode
import cn.dev33.satoken.stp.StpUtil
import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
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
import zip.ntoj.server.model.L
import zip.ntoj.server.model.Problem
import zip.ntoj.server.model.ProblemSample
import zip.ntoj.server.model.R
import zip.ntoj.server.service.ProblemService
import zip.ntoj.server.service.UserService
import java.time.Instant

@RestController
@RequestMapping("/admin/problem")
@SaCheckLogin
@SaCheckRole(value = ["COACH", "ADMIN", "SUPER_ADMIN"], mode = SaMode.OR)
class AdminProblemController(
    val problemService: ProblemService,
    val userService: UserService,
) {
    @GetMapping("{id}")
    fun get(@PathVariable id: Long): ResponseEntity<R<ProblemDto>> {
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
                        testCases = null,
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
    fun delete(@PathVariable id: Long): ResponseEntity<R<Void>> {
        if (!problemService.exists(id)) return R.fail(404, "题目不存在")
        problemService.delete(id)
        return R.success(200, "删除成功")
    }
}

data class ProblemRequest(
    @field:NotEmpty(message = "题号不能为空") val alias: String?,
    @field:NotEmpty(message = "标题不能为空") val title: String?,
    val background: String?,
    val description: String?,
    val inputDescription: String?,
    val outputDescription: String?,
    val timeLimit: Int? = 1000,
    val memoryLimit: Int? = 64,
    val samples: List<ProblemSample> = mutableListOf(),
    val note: String?,
    val visible: Boolean? = null,
)

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
    val visible: Boolean?,
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
            visible = problem.visible,
        )
    }
}
