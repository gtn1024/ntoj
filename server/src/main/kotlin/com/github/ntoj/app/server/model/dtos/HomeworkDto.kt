package com.github.ntoj.app.server.model.dtos

import com.github.ntoj.app.server.model.entities.Homework
import com.github.ntoj.app.server.model.entities.Problem
import java.io.Serializable
import java.time.Instant

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
            fun from(problem: Problem) =
                ProblemDto(
                    problem.title,
                    problem.alias,
                    problem.problemId!!,
                )
        }
    }
}
