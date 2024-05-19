package com.github.ntoj.app.shared.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

enum class SubmissionStatus {
    PENDING, // 0
    JUDGING, // 1
    ACCEPTED, // 2
    WRONG_ANSWER, // 3
    TIME_LIMIT_EXCEEDED, // 4
    MEMORY_LIMIT_EXCEEDED, // 5
    RUNTIME_ERROR, // 6
    COMPILE_ERROR, // 7
    SYSTEM_ERROR, // 8
    PRESENTATION_ERROR, // 9
    OUTPUT_LIMIT_EXCEEDED, // 10
    DEPRECATED, // 11
    ;

    companion object {
        @JsonCreator
        @JvmStatic
        fun fromValue(value: String): SubmissionStatus {
            return when (value) {
                "PENDING" -> PENDING
                "JUDGING" -> JUDGING
                "ACCEPTED" -> ACCEPTED
                "WRONG_ANSWER" -> WRONG_ANSWER
                "TIME_LIMIT_EXCEEDED" -> TIME_LIMIT_EXCEEDED
                "MEMORY_LIMIT_EXCEEDED" -> MEMORY_LIMIT_EXCEEDED
                "RUNTIME_ERROR" -> RUNTIME_ERROR
                "COMPILE_ERROR" -> COMPILE_ERROR
                "SYSTEM_ERROR" -> SYSTEM_ERROR
                "PRESENTATION_ERROR" -> PRESENTATION_ERROR
                "OUTPUT_LIMIT_EXCEEDED" -> OUTPUT_LIMIT_EXCEEDED
                "DEPRECATED" -> DEPRECATED
                else -> throw IllegalArgumentException("Unknown submission status: $value")
            }
        }
    }
}

data class TestcaseJudgeResult(
    @JsonProperty("status") val status: SubmissionStatus,
    @JsonProperty("time") val time: Long,
    @JsonProperty("memory") val memory: Long,
    @JsonProperty("input", required = false) val input: String?,
    @JsonProperty("output", required = false) val output: String?,
)
