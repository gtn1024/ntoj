package com.github.ntoj.app.server.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.github.ntoj.app.server.config.Constant
import com.github.ntoj.app.server.config.InformationConfig
import com.github.ntoj.app.server.ext.success
import com.github.ntoj.app.shared.model.R
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/")
class WelcomeController(
    val informationConfig: InformationConfig,
) {
    @RequestMapping("")
    fun welcome(): Map<String, String> {
        return mapOf(
            "message" to "Welcome to NTOJ Server!",
            "serverTime" to
                LocalDateTime.now(ZoneId.of("Asia/Shanghai")).format(
                    DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
                ),
            "version" to Constant.VERSION,
        )
    }

    @GetMapping("/info")
    fun info(): ResponseEntity<R<InformationConfig>> {
        return R.success(200, "获取成功", informationConfig)
    }
}
