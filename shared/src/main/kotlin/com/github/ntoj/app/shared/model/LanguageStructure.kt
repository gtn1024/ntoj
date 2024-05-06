package com.github.ntoj.app.shared.model

data class LanguageStructure(
    val display: String,
    val execute: String,
    val highlight: String?,
    val compileTimeLimit: Number?,
    val disabled: Boolean = false,
    val compile: String?,
    val editor: String?,
    val source: String = "foo",
    val target: String = "bar",
    val timeLimitRate: Number = 1,
    val memoryLimitRate: Number = 1,
)
