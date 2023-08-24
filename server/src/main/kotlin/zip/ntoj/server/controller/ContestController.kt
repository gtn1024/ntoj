package zip.ntoj.server.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import zip.ntoj.server.ext.success
import zip.ntoj.server.model.Contest
import zip.ntoj.server.model.L
import zip.ntoj.server.service.ContestService
import zip.ntoj.shared.model.R

@RestController
@RequestMapping("/contest")
class ContestController(
    private val contestService: ContestService,
) {
    @GetMapping
    fun index(
        @RequestParam(required = false, defaultValue = "1") current: Int,
        @RequestParam(required = false, defaultValue = "20") pageSize: Int,
    ): ResponseEntity<R<L<ContestDto>>> {
        val list = contestService.get(page = current, pageSize = pageSize)
        val count = contestService.count()
        return R.success(
            200,
            "获取成功",
            L(
                total = count,
                page = current,
                list = list.map { ContestDto.from(it) },
            ),
        )
    }

    data class ContestDto(
        val id: Long,
        val title: String,
        val description: String?,
    ) {
        companion object {
            fun from(contest: Contest) = ContestDto(
                id = contest.contestId!!,
                title = contest.title,
                description = contest.description,
            )
        }
    }
}
