package zip.ntoj.server.controller

import com.fasterxml.jackson.annotation.JsonFormat
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
import java.time.Instant

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
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") val startTime: Instant,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") val endTime: Instant,
        val type: Contest.ContestType,
        val permission: Contest.ContestPermission,
        val userCount: Int,
    ) {
        companion object {
            fun from(contest: Contest) = ContestDto(
                id = contest.contestId!!,
                title = contest.title,
                description = contest.description,
                startTime = contest.startTime,
                endTime = contest.endTime,
                type = contest.type,
                permission = contest.permission,
                userCount = contest.users.size,
            )
        }
    }
}
