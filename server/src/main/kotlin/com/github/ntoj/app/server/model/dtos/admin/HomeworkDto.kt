package com.github.ntoj.app.server.model.dtos.admin

import com.github.ntoj.app.server.model.entities.Homework
import com.github.ntoj.app.server.model.entities.Problem
import java.io.Serializable
import java.time.Instant

/**
 * DTO for {@link com.github.ntoj.app.server.model.entities.Homework}
 */
data class HomeworkDto(
    val title: String,
    val startTime: Instant,
    val endTime: Instant,
    val problems: List<ProblemDto>,
    val id: Long,
) : Serializable {
    companion object {
        fun from(homework: Homework) =
            HomeworkDto(
                homework.title,
                homework.startTime,
                homework.endTime,
                homework.problems.map { ProblemDto.from(it) },
                homework.homeworkId!!,
            )
    }

    data class ProblemDto(val title: String, val alias: String, val id: Long) : Serializable {
        companion object {
            fun from(problem: Problem): ProblemDto {
                return ProblemDto(problem.title, problem.alias, problem.problemId!!)
            }
        }
    }
}
