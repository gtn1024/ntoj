package com.github.ntoj.app.server.model.dtos

import com.github.ntoj.app.server.model.entities.Problem
import com.github.ntoj.app.server.model.entities.Record
import com.github.ntoj.app.shared.model.JudgeStage
import com.github.ntoj.app.shared.model.RecordOrigin
import com.github.ntoj.app.shared.model.SubmissionStatus
import com.github.ntoj.app.shared.model.TestcaseJudgeResult
import java.io.Serializable
import java.time.Instant

data class RecordDto(
    val createdAt: Instant,
    val user: UserDto,
    val problem: ProblemDto?,
    val origin: RecordOrigin,
    val lang: String,
    val code: String,
    val status: SubmissionStatus,
    val stage: JudgeStage,
    val time: Int?,
    val memory: Int?,
    val compileLog: String?,
    val testcaseResult: List<TestcaseJudgeResult>,
    val id: String,
) : Serializable {
    data class ProblemDto(
        val title: String,
        val alias: String,
    ) : Serializable {
        companion object {
            fun from(problem: Problem) =
                ProblemDto(
                    title = problem.title,
                    alias = problem.alias,
                )
        }
    }

    companion object {
        fun from(record: Record) =
            RecordDto(
                createdAt = record.createdAt!!,
                user = UserDto.from(record.user),
                problem = record.problem?.let { ProblemDto.from(it) },
                origin = record.origin,
                lang = record.lang,
                code = record.code,
                status = record.status,
                stage = record.stage,
                time = record.time,
                memory = record.memory,
                compileLog = record.compileLog,
                testcaseResult = record.testcaseResult,
                id = record.recordId!!,
            )
    }
}
