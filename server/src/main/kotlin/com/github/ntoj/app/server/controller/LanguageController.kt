package com.github.ntoj.app.server.controller

import com.github.ntoj.app.server.ext.success
import com.github.ntoj.app.shared.model.LanguageStructure
import com.github.ntoj.app.shared.model.R
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/language")
class LanguageController(
    private val languages: Map<String, LanguageStructure>,
) {
    @GetMapping
    fun index(): ResponseEntity<R<Map<String, LanguageStructure>>> {
        return R.success(200, "获取成功", languages)
    }
}
