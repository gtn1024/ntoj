package com.github.ntoj.app.server.controller

import com.github.ntoj.app.server.config.system.LanguageMap
import com.github.ntoj.app.server.ext.success
import com.github.ntoj.app.shared.model.R
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/language")
class LanguageController(
    private val languages: LanguageMap,
) {
    @GetMapping
    fun index(): ResponseEntity<R<LanguageMap>> {
        return R.success(200, "获取成功", languages)
    }
}
